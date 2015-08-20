package org.jboss.fuse.qa.fafram8.resource;

import org.jboss.fuse.qa.fafram8.deployer.Deployer;
import org.jboss.fuse.qa.fafram8.deployer.LocalDeployer;
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

	// Host property
	private static final String HOST = System.getProperty("host");

	// Fuse username
	private static final String USER = System.getProperty("fuse.user", "admin");

	// Fuse password
	private static final String PASSWORD = System.getProperty("fuse.password", "admin");

	@Override
	protected void before() {
		setup();
	}

	@Override
	protected void after() {
		tearDown();
	}

	public void setup() {
		if (HOST == null) {
			log.info("Setting up local deployment");
			setupLocalDeployment();
		} else {
			log.info("Setting up remote deployment on host " + HOST);
			setupRemoteDeployment();
		}
	}

	public void tearDown() {
		deployer.tearDown();
	}

	private void setupLocalDeployment() {
		// Create a local deployer with local SSH Client and assign to deployer variable
		deployer = new LocalDeployer(new FuseSSHClient().hostname("localhost").port(8101).username(USER)
				.password(PASSWORD));

		// Start deployer
		deployer.setup();
	}

	private void setupRemoteDeployment() {

	}
}