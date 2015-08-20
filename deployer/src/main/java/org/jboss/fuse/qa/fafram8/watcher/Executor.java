package org.jboss.fuse.qa.fafram8.watcher;

import org.jboss.fuse.qa.fafram8.ssh.AbstractSSHClient;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Watcher class.
 * Created by avano on 19.8.15.
 */
@AllArgsConstructor
@Slf4j
public class Executor {
	private AbstractSSHClient client;

	public String executeCommand(String cmd) {
		return cmd;
	}

	public void waitForBoot() {
		boolean online = false;

		int elapsed = 0;

		while (!online) {
			// Check if the time is up
			if (elapsed > 120) {
				log.error("*** Connection couldn't be established after " + 120 + " seconds");
				throw new RuntimeException("Connection couldn't be established after " + 120 + " seconds");
			}

			try {
				// Try to execute the command - if it throws an exception, it is not ready yet
				client.connect();
				online = true;
				log.info("* Container online");
			} catch (Exception ex) {
				log.debug("** Remaining time: " + (120 - elapsed) + " seconds. ");
				elapsed += 3;
			}
			sleep(3000L);
		}
	}

	public void waitForShutdown() {
		boolean online = true;

		int elapsed = 0;

		while (online) {
			// Check if the time is up
			if (elapsed > 30) {
				log.error("*** Connection could be established after " + 30 + " seconds");
				throw new RuntimeException("Connection could be established after " + 30 + " seconds");
			}

			try {
				// Try to execute the command - if it succeed, the container is still up
				online = client.isConnected();
				log.debug("** Remaining time: " + (30 - elapsed) + " seconds. ");
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
