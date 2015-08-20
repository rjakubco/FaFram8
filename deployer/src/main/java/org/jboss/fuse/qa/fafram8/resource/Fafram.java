package org.jboss.fuse.qa.fafram8.resource;

import org.jboss.fuse.qa.fafram8.deployer.Deployer;
import org.jboss.fuse.qa.fafram8.deployer.LocalDeployer;
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
		if (SystemProperty.HOST == null) {
			log.info("Setting up local deployment");
			setupLocalDeployment();
		} else {
			log.info("Setting up remote deployment on host " + SystemProperty.HOST);
			setupRemoteDeployment();
		}
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

		// Start deployer
		deployer.setup();
	}

	/**
	 * Sets up the remote deployment.
	 */
	private void setupRemoteDeployment() {

	}
}