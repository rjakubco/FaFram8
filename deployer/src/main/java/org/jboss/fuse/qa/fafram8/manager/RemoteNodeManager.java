package org.jboss.fuse.qa.fafram8.manager;

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
		SystemProperty.set(FaframConstant.FUSE_PATH, productPath);
	}

	@Override
	public void prepareFuse() {
		ModifierExecutor.executeModifiers(executor);
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
	public void stopAndClean(boolean ignoreExceptions) {
		SystemProperty.clearAllProperties();
		ModifierExecutor.clearAllModifiers();
		stop();
	}

	/**
	 * Stops all karaf instances and removes them.
	 */
	public void stop() {
		log.info("Cleaning " + SystemProperty.getHost());
		executor.executeCommand("pkill -9 -f karaf");
		executor.executeCommand("rm -rf " + SystemProperty.getFaframFolder());
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
