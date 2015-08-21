package org.jboss.fuse.qa.fafram8.executor;

import org.jboss.fuse.qa.fafram8.exceptions.KarafSessionDownException;
import org.jboss.fuse.qa.fafram8.exceptions.SSHClientException;
import org.jboss.fuse.qa.fafram8.exceptions.VerifyFalseException;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;
import org.jboss.fuse.qa.fafram8.ssh.AbstractSSHClient;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Executor class.
 * Created by avano on 19.8.15.
 */
@AllArgsConstructor
@Slf4j
public class Executor {
	private AbstractSSHClient client;

	/**
	 * Executes command.
	 *
	 * @param cmd command
	 * @return command response
	 */
	public String executeCommand(String cmd) {
		try {
			return client.executeCommand(cmd);
			// TODO: rework this
		} catch (KarafSessionDownException e) {
			try {
				client.connect();
			} catch (VerifyFalseException e1) {
				e1.printStackTrace();
			} catch (SSHClientException e1) {
				e1.printStackTrace();
			}
		} catch (SSHClientException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Checks if the client can connect.
	 */
	public boolean canConnect() {
		try {
			client.connect();
			return true;
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * Waits for the container to boot.
	 */
	public void waitForBoot() {
		boolean online = false;

		int elapsed = 0;

		while (!online) {
			// Check if the time is up
			if (elapsed > SystemProperty.START_WAIT_TIME) {
				log.error("Connection couldn't be established after " + SystemProperty.START_WAIT_TIME
						+ " seconds");
				throw new RuntimeException("Connection couldn't be established after "
						+ SystemProperty.START_WAIT_TIME + " seconds");
			}

			try {
				// Try to execute the command - if it throws an exception, it is not ready yet
				client.connect();
				online = true;
				log.info("Container online");
			} catch (Exception ex) {
				log.debug("Remaining time: " + (SystemProperty.START_WAIT_TIME - elapsed) + " seconds. ");
				elapsed += 3;
			}
			sleep(3000L);
		}
	}

	/**
	 * Waits for the container to shut down.
	 */
	public void waitForShutdown() {
		boolean online = true;

		int elapsed = 0;

		while (online) {
			// Check if the time is up
			if (elapsed > SystemProperty.STOP_WAIT_TIME) {
				log.error("Connection could be established after " + SystemProperty.STOP_WAIT_TIME + " seconds");
				throw new RuntimeException(
						"Connection could be established after " + SystemProperty.STOP_WAIT_TIME + " seconds");
			}

			try {
				// Try to execute the command - if it succeed, the container is still up
				online = client.isConnected();
				log.debug("Remaining time: " + (SystemProperty.STOP_WAIT_TIME - elapsed) + " seconds. ");
				elapsed += 5;
			} catch (Exception ex) {
				online = false;
			}

			sleep(5000L);
		}
	}

	/**
	 * Waits for container provisioning.
	 *
	 * @param containerName container name
	 */
	public void waitForProvisioning(String containerName) {
		// Wait before executing - sometimes the provision is triggered a bit later
		sleep(10000l);
		int retries = 0;
		String container;
		boolean isSuccessful = false;

		while (!isSuccessful) {
			if (retries > SystemProperty.PROVISION_WAIT_TIME) {
				log.error("Container root failed to provision in time");
				throw new RuntimeException("*** Container root failed to provision in time");
			}

			container = executeCommand("container-list | grep " + containerName);
			isSuccessful = container != null && container.contains("success");

			if (!isSuccessful) {
				log.debug("** Remaining time: " + (SystemProperty.PROVISION_WAIT_TIME - retries) + " seconds. ");
				retries += 3;
				try {
					Thread.sleep(3000L);
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Sleeps for given amount of time.
	 *
	 * @param time time in millis
	 */
	private void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
