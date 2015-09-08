package org.jboss.fuse.qa.fafram8.resource;

import org.jboss.fuse.qa.fafram8.deployer.Deployer;
import org.jboss.fuse.qa.fafram8.deployer.LocalDeployer;
import org.jboss.fuse.qa.fafram8.deployer.RemoteDeployer;
import org.jboss.fuse.qa.fafram8.exceptions.SSHClientException;
import org.jboss.fuse.qa.fafram8.manager.ContainerManager;
import org.jboss.fuse.qa.fafram8.manager.LocalNodeManager;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;
import org.jboss.fuse.qa.fafram8.ssh.FuseSSHClient;
import org.jboss.fuse.qa.fafram8.ssh.NodeSSHClient;

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
			log.info("Setting up remote deployment on host " + SystemProperty.HOST + ":" + SystemProperty.HOST_PORT);
			try {
				setupRemoteDeployment();
			} catch (SSHClientException e) {
				e.printStackTrace();
			}
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
	private void setupRemoteDeployment() throws SSHClientException {
		deployer = new RemoteDeployer(new NodeSSHClient().hostname(SystemProperty.HOST).port(SystemProperty.HOST_PORT)
				.username(SystemProperty.HOST_USER).password(SystemProperty.HOST_PASSWORD),
				new FuseSSHClient().hostname(SystemProperty.HOST).fuseSSHPort().username(SystemProperty.FUSE_USER)
						.password(SystemProperty.FUSE_USER));
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

	public String executeFuseCommand(String command) {
		return deployer.getContainerManager().getExecutor().executeCommand(command);
	}

	/**
	 * Adds a new user.
	 *
	 * @param user user
	 * @param password password
	 * @param roles comma-separated roles
	 * @return this
	 */
	public Fafram addUser(String user, String password, String roles) {
		deployer.getNodeManager().addUser(user, password, roles);
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
		deployer.getNodeManager().replaceFile(fileToReplace, fileToUse);
		return this;
	}

	/**
	 * Provide deployment with Fabric environment
	 * @return
	 */
	public Fafram withFabric() {
		deployer.getContainerManager().setFabric(true);
		return this;
	}
}
