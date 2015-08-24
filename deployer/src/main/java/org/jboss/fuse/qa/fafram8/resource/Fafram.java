package org.jboss.fuse.qa.fafram8.resource;

import org.jboss.fuse.qa.fafram8.deployer.Deployer;
import org.jboss.fuse.qa.fafram8.deployer.LocalDeployer;
import org.jboss.fuse.qa.fafram8.manager.LocalNodeManager;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;
import org.jboss.fuse.qa.fafram8.ssh.FuseSSHClient;

import org.junit.rules.ExternalResource;

import lombok.extern.slf4j.Slf4j;

/**
 * Fafram resource class.
 * Created by avano on 19.8.15.
 */
@Slf4j
public class Fafram extends ExternalResource {
	// Deployer instance
	private Deployer deployer;

	public Fafram() {
		if (SystemProperty.HOST == null) {
			log.info("Setting up local deployment");
			setupLocalDeployment();
		} else {
			log.info("Setting up remote deployment on host " + SystemProperty.HOST);
			setupRemoteDeployment();
		}
	}

	@Override
	protected void before() {
		setup();
	}

	@Override
	protected void after() {
		tearDown();
	}

	/**
	 * Start method.
	 */
	public void setup() {
		// Start deployer
		deployer.setup();
	}

	/**
	 * Stop method.
	 */
	public void tearDown() {
		deployer.tearDown();
	}

	/**
	 * Sets up the local deployment.
	 */
	private void setupLocalDeployment() {
		// Create a local deployer with local SSH Client and assign to deployer variable
		deployer = new LocalDeployer(new FuseSSHClient().hostname("localhost").port(8101).username(SystemProperty
				.FUSE_USER).password(SystemProperty.FUSE_PASSWORD));
	}

	/**
	 * Sets up the remote deployment.
	 */
	private void setupRemoteDeployment() {
		throw new UnsupportedOperationException("not implemented");
	}

	/**
	 * Executes a command.
	 *
	 * @param command command
	 * @return command response
	 */
	public String executeCommand(String command) {
		return deployer.getNodeManager().getExecutor().executeCommand(command);
	}

	/**
	 * Adds a new user.
	 *
	 * @param user user
	 * @param pass pass
	 * @param roles comma-separated roles
	 * @return this
	 */
	public Fafram addUser(String user, String pass, String roles) {
		((LocalNodeManager) deployer.getNodeManager()).addUser(user, pass, roles);
		return this;
	}

	/**
	 * Replaces a file.
	 *
	 * @param fileToReplace file to replace
	 * @param fileToUse file to use
	 * @return this
	 */
	public Fafram replaceFile(String fileToReplace, String fileToUse) {
		((LocalNodeManager) deployer.getNodeManager()).replaceFile(fileToReplace, fileToUse);
		return this;
	}

	public Fafram withFabric() {
		((LocalNodeManager) deployer.getNodeManager()).setFabric(true);
		return this;
	}
}
