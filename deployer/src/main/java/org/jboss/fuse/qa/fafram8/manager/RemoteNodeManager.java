package org.jboss.fuse.qa.fafram8.manager;

import static org.jboss.fuse.qa.fafram8.modifier.impl.RemoteFileModifier.moveRemoteFile;
import static org.jboss.fuse.qa.fafram8.modifier.impl.RemotePropertyModifier.putRemoteProperty;

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
	 * Constructor
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
		// TODO workingDir on perf?
		log.info("Preparing zip");
		executor.executeCommand("mkdir " + SystemProperty.FAFRAM_FOLDER);
		productZipPath = Downloader.getProduct(executor);
		log.debug("Zip path is " + productZipPath);
	}

	@Override
	public void unzipArtifact() {
		log.info("Unzipping fuse from " + productZipPath);

		log.debug(executor.executeCommand("unzip -q -d " + SystemProperty.FAFRAM_FOLDER + " " + productZipPath));
		productPath = executor.executeCommand("ls -d $PWD" + SEP + SystemProperty.FAFRAM_FOLDER + SEP + "*" + SEP);

		log.debug("Product path is " + productPath);
		System.setProperty(FaframConstant.FUSE_PATH, productPath);
	}

	@Override
	public void prepareFuse() {
		// Add default user
		modifierExecutor.addModifiers(putRemoteProperty("etc/users.properties", SystemProperty.FUSE_USER,
				SystemProperty.FUSE_PASSWORD + ",admin,manager,viewer,Monitor, Operator, Maintainer, Deployer, " +
						"Auditor, Administrator, SuperUser", executor));

		modifierExecutor.executeModifiers();
	}

	@Override
	public void startFuse() {
		try {
			// TODO add changing java before start
			log.info("Starting fuse");
			executor.executeCommand(productPath + SEP + "bin" + SEP + "start");
			fuseExecutor.waitForBoot();
		} catch (Exception e) {
			stopAndClean();
			throw new RuntimeException("Could not start container: " + e);
		}
	}

	/**
	 * Kills Karaf and deletes the fafram folder on remote host
	 */
	public void stopAndClean() {
		log.info("Cleaning " + SystemProperty.HOST);
		executor.executeCommand("pkill -9 -f karaf");
		executor.executeCommand("rm -rf " + SystemProperty.FAFRAM_FOLDER);
	}

	/**
	 * Adds/Replaces property in given file
	 *
 	 * @param path path to file where property should be set
	 * @param key key of the property
	 * @param value value of the propety
	 */
	public void addProperty(String path, String key, String value) {
		this.modifierExecutor.addModifiers(putRemoteProperty(path, key, value, executor));
	}

	/**
	 * Adds a new user.
	 *
	 * @param user user
	 * @param pass password
	 * @param roles comma-separated roles
	 */
	public void addUser(String user, String pass, String roles) {
		this.modifierExecutor.addModifiers(putRemoteProperty("etc/users.properties", user, pass + "," + roles, executor));
	}

	/**
	 * Replaces file.
	 *
	 * @param fileToReplace file from localhost
	 * @param fileToUse file path on remote
	 */
	public void replaceFile(String fileToReplace, String fileToUse) {
		this.modifierExecutor.addModifiers(moveRemoteFile(fileToReplace, fileToUse, executor));
	}
}
