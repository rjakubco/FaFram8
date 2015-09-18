package org.jboss.fuse.qa.fafram8.manager;

import static org.jboss.fuse.qa.fafram8.modifier.impl.FileModifier.moveFile;
import static org.jboss.fuse.qa.fafram8.modifier.impl.PropertyModifier.putProperty;

import org.jboss.fuse.qa.fafram8.downloader.Downloader;
import org.jboss.fuse.qa.fafram8.exceptions.SSHClientException;
import org.jboss.fuse.qa.fafram8.executor.Executor;
import org.jboss.fuse.qa.fafram8.modifier.ModifierExecutor;
import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;
import org.jboss.fuse.qa.fafram8.ssh.SSHClient;

import java.io.File;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Remote node manager class.
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
@Slf4j
public class RemoteNodeManager implements NodeManager {

	// executor to node(remote host)
	@Getter
	private Executor executor;

	// executor for Fuse on remote host
	@Getter
	private Executor fuseExecutor;

	// File separator
	private static final String SEP = File.separator;

	// Product zip path
	private String productZipPath;

	// Full path to unzipped product
	private String productPath;

	// Modifier executor
	private ModifierExecutor modifierExecutor = new ModifierExecutor();

	/**
	 * Constructor.
	 *
	 * @param nodeClient sshClient to remote host
	 * @param fuseClient sshClient to fuse on remote host
	 * @throws SSHClientException if there is some serious problem with ssh
	 */
	public RemoteNodeManager(SSHClient nodeClient, SSHClient fuseClient) throws SSHClientException {
		this.executor = new Executor(nodeClient);
		this.fuseExecutor = new Executor(fuseClient);
		executor.connect();
	}

	@Override
	public void prepareZip() {
		log.info("Preparing zip...");
		executor.executeCommand("mkdir " + getFolder());
		productZipPath = Downloader.getProduct(executor);
		log.debug("Zip path is " + productZipPath);
	}

	@Override
	public void unzipArtifact() {
		log.info("Unzipping fuse from " + productZipPath);

		log.debug(executor.executeCommand("unzip -q -d " + getFolder() + " " + productZipPath));
		productPath = executor.executeCommand("ls -d $PWD" + SEP + getFolder() + SEP + "*" + SEP);

		log.debug("Product path is " + productPath);
		System.setProperty(FaframConstant.FUSE_PATH, productPath);
	}

	@Override
	public void prepareFuse() {
		// Add default user
		modifierExecutor.addModifiers(putProperty("etc/users.properties", SystemProperty.getFuseUser(),
				SystemProperty.getFusePassword() + ",admin,manager,viewer,Monitor, Operator, Maintainer, Deployer, "
						+ "Auditor, Administrator, SuperUser", executor));

		modifierExecutor.executeModifiers();
	}

	@Override
	public void startFuse() {
		try {
			// TODO(rjakubco): add changing java before start
			log.info("Starting fuse");
			executor.executeCommand(productPath + SEP + "bin" + SEP + "start");
			fuseExecutor.waitForBoot();
		} catch (Exception e) {
			throw new RuntimeException("Could not start container: " + e);
		}
	}

	@Override
	public void stopAndClean() {
		log.info("Cleaning " + SystemProperty.getHost());
		executor.executeCommand("pkill -9 -f karaf");
		executor.executeCommand("rm -rf " + SystemProperty.getFaframFolder());

		System.clearProperty(FaframConstant.FABRIC);
		System.clearProperty(FaframConstant.FUSE_PATH);
	}

	@Override
	public void addProperty(String path, String key, String value) {
		this.modifierExecutor.addModifiers(putProperty(path, key, value, executor));
	}

	@Override
	public void addUser(String user, String pass, String roles) {
		this.modifierExecutor
				.addModifiers(putProperty("etc/users.properties", user, pass + "," + roles, executor));
	}

	@Override
	public void replaceFile(String fileToReplace, String fileToUse) {
		this.modifierExecutor.addModifiers(moveFile(fileToReplace, fileToUse, executor));
	}

	/**
	 * Creates folder path on remote machines.
	 * Checking if property fafram.working.directory is set.
	 *
	 * @return path where fafram8 folder should be created
	 */
	public static String getFolder() {
		String folder;
		if ("".equals(SystemProperty.getWorkingDirectory())) {
			folder = SystemProperty.getFaframFolder();
		} else {
			folder = SystemProperty.getWorkingDirectory() + SEP + SystemProperty.getFaframFolder();
		}
		return folder;
	}
}
