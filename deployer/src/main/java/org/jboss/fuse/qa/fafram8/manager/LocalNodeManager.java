package org.jboss.fuse.qa.fafram8.manager;

import static org.jboss.fuse.qa.fafram8.modifier.impl.AccessRightsModifier.setExecutable;
import static org.jboss.fuse.qa.fafram8.modifier.impl.FileModifier.moveFile;
import static org.jboss.fuse.qa.fafram8.modifier.impl.PropertyModifier.putProperty;

import org.apache.commons.io.FileUtils;

import org.jboss.fuse.qa.fafram8.downloader.Downloader;
import org.jboss.fuse.qa.fafram8.modifier.ModifierExecutor;
import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;
import org.jboss.fuse.qa.fafram8.executor.Executor;
import org.jboss.fuse.qa.fafram8.ssh.SSHClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;

/**
 * Local node manager class.
 * Created by avano on 19.8.15.
 */
@Slf4j
public class LocalNodeManager implements NodeManager {
	@Getter
	private Executor executor;

	// File separator
	private static final String SEP = File.separator;

	// Is windows?
	private boolean windows = false;

	// Is AMQ?
	private boolean amq = false;

	// Flag if this instance was already stopped
	private boolean stopped = false;

	// Product zip path
	private String productZipPath;

	// Target dir path
	private String targetPath;

	// Full path to unzipped product
	private String productPath;

	// Container process
	private Process productProcess;

	// Modifier executor
	private ModifierExecutor modifierExecutor = new ModifierExecutor();

	// Setup fabric?
	@Setter
	private boolean fabric = false;

	// Is jenkins?
	private boolean jenkins = System.getenv("WORKSPACE") != null;

	/**
	 * Constructor.
	 *
	 * @param client ssh client
	 */
	public LocalNodeManager(SSHClient client) {
		executor = new Executor(client);
	}

	/**
	 * Checks if some container is already running.
	 */
	public void checkRunningContainer() {
		if (executor.canConnect()) {
			log.warn("Other container instance is already running! Unpredictable results may occur!");
		}
	}

	@Override
	public void prepareZip() {
		productZipPath = Downloader.getProduct();
	}

	@Override
	public void unzipArtifact() {
		// Fix for long jenkins paths
		if (jenkins) {
			targetPath = new File(System.getenv("WORKSPACE") + SEP + new Date().getTime()).getAbsolutePath();
		}
		else {
			targetPath = new File("target" + SEP + "container" + SEP + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
					.format(new Date())).getAbsolutePath();
		}

		log.debug("Unzipping to " + targetPath);

		try {
			ZipFile zipFile = new ZipFile(new File(productZipPath).getAbsolutePath());
			zipFile.extractAll(targetPath);
		} catch (Exception ex) {
			log.error("Exception caught during unzipping!");
			throw new RuntimeException(ex);
		}

		// Construct the full path to product root - get the subdir name in targetPath
		String folderName = new File(targetPath).list()[0];

		// Use the subdir name to construct the product path
		productPath = targetPath + SEP + folderName;
		log.debug("Product path is " + productPath);
		System.setProperty(FaframConstant.FUSE_PATH, productPath);
	}

	@Override
	public void prepareFuse() {
		if (!windows) {
			// Restore execute rights to karaf, start, stop
			log.debug("Setting executable flags to karaf, start, stop");
			modifierExecutor.addModifiers(setExecutable("bin" + SEP + "karaf", "bin" + SEP + "start",
					"bin" + SEP + "stop"));
		}

		// Add default user
		modifierExecutor.addModifiers(putProperty("etc/users.properties", SystemProperty.FUSE_USER,
				SystemProperty.FUSE_PASSWORD + ",admin,manager,viewer,Monitor, Operator, Maintainer, Deployer, " +
						"Auditor, Administrator, SuperUser"));

		modifierExecutor.executeModifiers();
	}

	@Override
	public void startFuse() {
		String executable = "start";
		String extension = windows ? ".bat" : "";

		log.debug("Executable file is \"" + executable + extension + "\"");

		// Construct the path to the executable file
		String executablePath = productPath + SEP + "bin" + SEP + executable + extension;
		log.debug("Executing " + executablePath);

		try {
			if (SystemProperty.FUSE_ZIP != null) {
				// If we run custom zip
				log.info("Starting container");
			} else {
				// If we run artifact from mvn
				log.info("Starting " + (amq ? "A-MQ" : "Fuse") + " " + SystemProperty.FUSE_VERSION);
			}
			productProcess = Runtime.getRuntime().exec(executablePath);
			log.info("Waiting for the container to be online");
			executor.waitForBoot();
		} catch (Exception e) {
			stopAndClean();
			throw new RuntimeException("Could not start container: " + e);
		}

		if (fabric) {
			setupFabric();
		}
	}

	/**
	 * Sets up fabric.
	 */
	private void setupFabric() {
		executor.executeCommand("fabric:create");
		try {
			executor.waitForProvisioning("root");
		} catch (RuntimeException ex) {
			// Container is not provisioned in time
			stopAndClean();
			throw new RuntimeException("Container did not provision in time");
		}

		// Set system property to indicate that we are working with fabric
		System.setProperty("fabric", "");
	}

	/**
	 * Detects platform and product.
	 */
	public void detectPlatformAndProduct() {
		if (System.getProperty("os.name").startsWith("Windows")) {
			windows = true;
			log.debug("We're on Windows");
		} else {
			log.debug("We're on Unix");
		}

		if (SystemProperty.FUSE_ID.contains("a-mq")) {
			log.debug("We're working with A-MQ");
			amq = true;
		} else {
			log.debug("We're working with FUSE");
		}
	}

	/**
	 * Stops the container and cleans up if desired.
	 */
	public void stopAndClean() {
		if (!stopped) {
			stop();
			deleteTargetDir();
		}
	}

	/**
	 * Stops the container.
	 */
	private void stop() {
		String executable = "stop";
		String extension = windows ? ".bat" : "";

		log.debug("Executable file is \"" + executable + extension + "\"");

		String executablePath = productPath + SEP + "bin" + SEP + executable + extension;
		log.debug("Executing " + executablePath);

		try {
			if (SystemProperty.FUSE_ZIP != null) {
				// If we run custom zip
				log.info("Stopping container");
			} else {
				// If we run artifact from mvn
				log.info("Stopping " + (amq ? "A-MQ" : "Fuse") + " " + SystemProperty.FUSE_VERSION);
			}
			Runtime.getRuntime().exec(executablePath).waitFor();
			executor.waitForShutdown();
		} catch (Exception e) {
			// If we get an exception during shutdown, kill the process at OS level
			if (productProcess != null) {
				productProcess.destroy();
				try {
					productProcess.waitFor();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			// Throw the exception because something was wrong
			throw new RuntimeException("Could not stop container: " + e);
		}

		stopped = true;
	}

	/**
	 * Force-Delete target dir.
	 */
	private void deleteTargetDir() {
		if (SystemProperty.KEEP_FOLDER == null) {
			try {
				log.debug("Deleting " + targetPath);
				FileUtils.forceDelete(new File(targetPath));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds a new user.
	 *
	 * @param user user
	 * @param pass password
	 * @param roles comma-separated roles
	 */
	public void addUser(String user, String pass, String roles) {
		this.modifierExecutor.addModifiers(putProperty("etc/users.properties", user, pass + "," + roles));
	}

	public void replaceFile(String fileToReplace, String fileToUse) {
		this.modifierExecutor.addModifiers(moveFile(fileToReplace, fileToUse));
	}
}
