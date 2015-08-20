package org.jboss.fuse.qa.fafram8.manager;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.apache.maven.shared.invoker.PrintStreamHandler;

import org.jboss.fuse.qa.fafram8.ssh.AbstractSSHClient;
import org.jboss.fuse.qa.fafram8.watcher.Executor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;

/**
 * Local node manager class.
 * Created by avano on 19.8.15.
 */
@Slf4j
public class LocalNodeManager implements NodeManager {
	@Getter
	private Executor watcher;

	// File separator
	private static final String SEP = File.separator;

	private boolean windows = false;

	private boolean amq = false;

	private String localRepositoryPath;

	private String targetPath;

	// Full path to unzipped product
	private String productPath;

	// Fuse / A-MQ distro artifact
	private static final String GROUP = System.getProperty("fuse.group");
	private static final String ARTIFACT_NAME = System.getProperty("fuse.id");
	private static final String ARTIFACT_VERSION = System.getProperty("fuse.version");

	private Process productProcess;

	public LocalNodeManager(AbstractSSHClient client) {
		watcher = new Executor(client);
	}

	@Override
	public void prepareZip() {
		locateMaven();
		getMavenLocalRepository();
	}

	@Override
	public void prepareFuse() {
		unzipArtifact();
		addDefaultUser();
	}

	@Override
	public void startFuse() {
		String executable = "start";
		String extension = windows ? ".bat" : "";

		log.debug("** Executable file is \"" + executable + extension + "\"");

		// Construct the path to the executable file
		String executablePath = productPath + SEP + "bin" + SEP + executable + extension;
		log.debug("** Executing " + executablePath);

		try {
			if (System.getProperty("zip") != null) {
				// If we run custom zip
				log.info("* Starting container");
			} else {
				// If we run artifact from mvn
				log.info("* Starting " + (amq ? "A-MQ" : "Fuse") + " " + ARTIFACT_VERSION);
			}
			productProcess = Runtime.getRuntime().exec(executablePath);
			log.info("* Waiting for the container to be online");
			watcher.waitForBoot();
		} catch (Exception e) {
			stopAndClean();
			throw new RuntimeException("Could not start container: " + e);
		}
	}

	public void detectPlatformAndProduct() {
		if (System.getProperty("os.name").startsWith("Windows")) {
			windows = true;
			log.debug("** We're on Windows");
		} else {
			log.debug("** We're on Unix");
		}

		if (ARTIFACT_NAME.contains("a-mq")) {
			log.debug("** We're working with A-MQ");
			amq = true;
		} else {
			log.debug("** We're working with FUSE");
		}
	}

	/**
	 * Checks for the maven and sets the maven.home property used by maven-invoker.
	 */
	private void locateMaven() {
		log.debug("** maven.home property is " + System.getProperty("maven.home"));
		if (System.getProperty("maven.home") != null) {
			return;
		}

		log.debug("** M2_HOME system property is " + System.getProperty("M2_HOME"));
		if (System.getProperty("M2_HOME") != null) {
			System.setProperty("maven.home", System.getProperty("M2_HOME"));
			return;
		}

		log.debug("** M2_HOME env property is " + System.getenv("M2_HOME"));
		if (System.getenv("M2_HOME") != null) {
			System.setProperty("maven.home", System.getenv("M2_HOME"));
			return;
		}

		String path = System.getenv("PATH");
		String[] pathParts = path.split(File.pathSeparator);

		for (String part : pathParts) {
			log.debug("Checking path for mvn: " + part);
			if (part.contains("mvn") || part.contains("maven")) {
				// Strip the /bin from mvn path if found
				String mvnLocation;
				if (part.contains("bin")) {
					mvnLocation = part.substring(0, part.indexOf("bin"));
				} else {
					mvnLocation = part;
				}

				System.setProperty("maven.home", mvnLocation);
				log.debug("** Maven found in " + mvnLocation);

				return;
			}
		}
	}

	/**
	 * Gets the maven local repository path.
	 */
	private void getMavenLocalRepository() {
		// Get effective settings from maven
		InvocationRequest req = new DefaultInvocationRequest();
		req.setGoals(Collections.singletonList("help:effective-settings"));

		Invoker invoker = new DefaultInvoker();

		// Redirect invoker output to variable
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		invoker.setOutputHandler(new PrintStreamHandler(ps, true));

		try {
			invoker.execute(req);
		} catch (MavenInvocationException e) {
			e.printStackTrace();
		}

		// Close streams
		ps.close();
		String output = baos.toString();
		try {
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Parse local repository
		output = output.substring(output.indexOf("localRepository"));
		output = output.substring(output.indexOf(">") + 1, output.indexOf("<"));

		// Set local repository path
		localRepositoryPath = output;
		log.debug("** Local repository path is " + localRepositoryPath);
	}

	/**
	 * Gets the artifact path.
	 *
	 * @return artifact path
	 */
	private String getArtifactPath() {
		String groupPath = GROUP.replaceAll("\\.", SEP + SEP);

		// Construct the path to the artifact in local repo
		String artifactPath = localRepositoryPath + SEP + groupPath + SEP + ARTIFACT_NAME + SEP +
				ARTIFACT_VERSION + SEP + ARTIFACT_NAME + "-" + ARTIFACT_VERSION + ".zip";

		log.debug("** Artifact path is " + artifactPath);

		File artifact = new File(artifactPath);
		return artifact.getAbsolutePath();
	}

	/**
	 * Unzips the artifact from the local repository.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	private void unzipArtifact() {
		targetPath = new File("target" + SEP + "container" + SEP + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format
				(new Date())).getAbsolutePath();

		log.debug("** Unzipping to " + targetPath);
		try {
			ZipFile zipFile = new ZipFile(getArtifactPath());
			zipFile.extractAll(targetPath);
		} catch (Exception ex) {
			log.error("*** Exception caught during unzipping!");
			throw new RuntimeException(ex);
		}

		// Construct the full path to product root - get the subdir name in target/container/date/
		String folderName = new File(targetPath).list()[0];

		// Use the subdir name to construct the product path
		productPath = targetPath + SEP + folderName;
		log.debug("** Product path is " + productPath);

		// Restore execute rights to karaf, start, stop
		if (!windows) {
			log.debug("** Setting executable flags to karaf, start, stop");
			new File(productPath + SEP + "bin" + SEP + "karaf").setExecutable(true);
			new File(productPath + SEP + "bin" + SEP + "start").setExecutable(true);
			new File(productPath + SEP + "bin" + SEP + "stop").setExecutable(true);
		}
	}

	/**
	 * Stops the container and cleans up if desired.
	 */
	public void stopAndClean() {
		stop();
		deleteTargetDir();
	}

	/**
	 * Stops the container.
	 */
	private void stop() {
		String executable = "stop";
		String extension = windows ? ".bat" : "";

		log.debug("** Executable file is \"" + executable + extension + "\"");

		String executablePath = productPath + SEP + "bin" + SEP + executable + extension;
		log.debug("** Executing " + executablePath);

		try {
			if (System.getProperty("zip") != null) {
				// If we run custom zip
				log.info("* Stopping container");
			} else {
				// If we run artifact from mvn
				log.info("* Stopping " + (amq ? "A-MQ" : "Fuse") + " " + ARTIFACT_VERSION);
			}
			Runtime.getRuntime().exec(executablePath).waitFor();
			watcher.waitForShutdown();
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
	}

	/**
	 * Force-Delete target dir.
	 */
	private void deleteTargetDir() {
		// TODO(avano): keepfolder
		try {
			log.debug("** Deleting " + targetPath);
			FileUtils.forceDelete(new File(targetPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addDefaultUser() {
		try {
			FileWriter fw = new FileWriter(productPath + SEP + "etc" + SEP + "users.properties",
					true); // the true will append the new data
				log.debug("** Persisting default user");
				fw.write("\n" + "admin=admin,admin,manager,viewer,Monitor, Operator, Maintainer, Deployer, Auditor, Administrator, SuperUser");
			fw.close();
		} catch (IOException ex) {
			log.error("*** Error while adding a new user! " + ex);
			throw new RuntimeException("*** Error while adding a new user! " + ex);
		}
	}
}
