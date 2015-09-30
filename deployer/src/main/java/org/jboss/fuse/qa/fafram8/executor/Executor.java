package org.jboss.fuse.qa.fafram8.executor;

import org.jboss.fuse.qa.fafram8.exceptions.CopyFileException;
import org.jboss.fuse.qa.fafram8.exceptions.KarafSessionDownException;
import org.jboss.fuse.qa.fafram8.exceptions.SSHClientException;
import org.jboss.fuse.qa.fafram8.exceptions.VerifyFalseException;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;
import org.jboss.fuse.qa.fafram8.ssh.NodeSSHClient;
import org.jboss.fuse.qa.fafram8.ssh.SSHClient;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Executor class.
 * Created by avano on 19.8.15.
 */
@SuppressWarnings("UnnecessarySemicolon")
@AllArgsConstructor
@Slf4j
public class Executor {
	private SSHClient client;

	/**
	 * Executes command.
	 *
	 * @param cmd command
	 * @return command response
	 */
	@SuppressWarnings("TryWithIdenticalCatches")
	public String executeCommand(String cmd) {
		try {
			return client.executeCommand(cmd, false);
		} catch (KarafSessionDownException e) {
			log.error("Karaf session is down!");
		} catch (SSHClientException e) {
			log.error("SSHClient exception thrown: " + e);
		}

		return null;
	}

	/**
	 * Checks if the client can connect.
	 *
	 * @return true/false if can/couldn't connect
	 */
	public boolean canConnect() {
		try {
			// We just check if its possible to connect - supress exception
			client.connect(true);
			client.disconnect();
			return true;
		} catch (Exception ignored) {
			return false;
		}
	}

	//TODO(rjakubco): wrong javadoc
	//TODO(ecervena): provide smarter canConnect loop + log something
	/**
	 * Checks if the client can connect.
	 *
	 * @throws SSHClientException if something went wrong
	 */
	public void connect() throws SSHClientException {
		try {
			while(!canConnect()) {
				System.out.println("waiting...");
				Thread.sleep(1000);
			}
			client.connect(false);
		} catch (VerifyFalseException ex) {
			// TODO(rjakubco): recursion -> bad idea?
			connect();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Waits for the container to boot.
	 */
	public void waitForBoot() {
		final int step = 3;
		final long timeout = step * 1000L;
		boolean online = false;

		int elapsed = 0;

		while (!online) {
			// Check if the time is up
			if (elapsed > SystemProperty.getStartWaitTime()) {
				log.error("Connection couldn't be established after " + SystemProperty.getStartWaitTime()
						+ " seconds");
				throw new RuntimeException("Connection couldn't be established after "
						+ SystemProperty.getStartWaitTime() + " seconds");
			}

			try {
				// Try to execute the command - if it throws an exception, it is not ready yet
				// Supress the exception here to reduce spam
				client.connect(true);
				online = true;
				log.info("Container online");
			} catch (Exception ex) {
				log.debug("Remaining time: " + (SystemProperty.getStartWaitTime() - elapsed) + " seconds. ");
				elapsed += step;
			}
			sleep(timeout);
		}
	}

	/**
	 * Waits for the container to shut down.
	 */
	public void waitForShutdown() {
		final int step = 5;
		final long timeout = (step * 1000L);
		boolean online = true;

		int elapsed = 0;

		while (online) {
			// Check if the time is up
			if (elapsed > SystemProperty.getStopWaitTime()) {
				log.error("Connection could be established after " + SystemProperty.getStopWaitTime() + " seconds");
				throw new RuntimeException(
						"Connection could be established after " + SystemProperty.getStopWaitTime() + " seconds");
			}

			try {
				// Check if we are still connected
				online = client.isConnected();
				log.debug("Remaining time: " + (SystemProperty.getStopWaitTime() - elapsed) + " seconds. ");
				elapsed += step;
			} catch (Exception ex) {
				online = false;
			}

			sleep(timeout);
		}
	}

	/**
	 * Waits for container provisioning.
	 *
	 * @param containerName container name
	 */
	public void waitForProvisioning(String containerName) {
		final int step = 3;
		final long timeout = step * 1000L;
		final long startTimeout = 10000L;

		// Wait before executing - sometimes the provision is triggered a bit later
		sleep(startTimeout);
		int retries = 0;
		String container;
		boolean isSuccessful = false;

		while (!isSuccessful) {
			if (retries > SystemProperty.getProvisionWaitTime()) {
				log.error("Container root failed to provision in time");
				throw new RuntimeException("Container root failed to provision in time");
			}

			String reason = "";

			try {
				container = client.executeCommand("container-list | grep " + containerName, true);
				isSuccessful = container != null && container.contains("success");
			} catch (Exception e) {
				// Get the reason
				reason = e.getMessage();

				// Re-init the ssh connection if it's not successful
				try {
					client.connect(true);
				} catch (Exception e1) {
					// Do nothing
					;
				}
			}

			if (!isSuccessful) {
				log.debug("Remaining time: " + (SystemProperty.getProvisionWaitTime() - retries) + " seconds. " + (""
						.equals(reason) ? "" : "(" + reason + ")"));
				retries += step;
				try {
					Thread.sleep(timeout);
				} catch (final Exception ex) {
					// Do nothing
					;
				}
			}
		}
	}

	/**
	 * Copies local file to specified location in Fuse folder on remote host.
	 *
	 * @param localPath absolute path to the file on local machine that should be copied
	 * @param remotePath path to destination inside Fuse folder where the file should be copied
	 * @throws CopyFileException if there was error in copying file
	 */
	public void copyFileToRemote(final String localPath, final String remotePath) throws CopyFileException {
		if (client instanceof NodeSSHClient) {
			((NodeSSHClient) client).copyFileToRemote(localPath, remotePath);
		} else {
			throw new CopyFileException("SSH client assigned to Executor is not instance of NodeSSHClient!");
		}
	}

	/**
	 * Waits for the patch to be applied.
	 *
	 * @param patchName patch name
	 */
	public void waitForPatch(String patchName) {
		final int step = 3;
		final long timeout = step * 1000L;
		int retries = 0;
		boolean isSuccessful = false;

		log.info("Waiting for patch to be installed");

		while (!isSuccessful) {
			if (retries > SystemProperty.getPatchWaitTime()) {
				log.error("Container failed to install patch after " + SystemProperty.getPatchWaitTime() + " seconds.");
				throw new RuntimeException(
						"Container failed to install patch after " + SystemProperty.getPatchWaitTime() + " seconds.");
			}

			String reason = "";

			// TODO(avano): command, connection established, remaining time
			try {
				isSuccessful = client.executeCommand("patch:list | grep " + patchName, true).contains("true");
			} catch (Exception e) {
				reason = e.getMessage();

				try {
					client.connect(true);
				} catch (Exception e1) {
					// Do nothing, ; for checkstyle
					;
				}
			}

			if (!isSuccessful) {
				log.debug("Remaining time: " + (SystemProperty.getPatchWaitTime() - retries) + " seconds. " + (""
						.equals(reason) ? "" : "(" + reason + ")"));
				retries += step;
			}
			sleep(timeout);
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
