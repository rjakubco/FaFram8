package org.jboss.fuse.qa.fafram8.watcher;

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
		return cmd;
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
