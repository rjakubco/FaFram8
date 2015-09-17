package org.jboss.fuse.qa.fafram8.ssh;

import org.jboss.fuse.qa.fafram8.exceptions.KarafSessionDownException;
import org.jboss.fuse.qa.fafram8.exceptions.SSHClientException;
import org.jboss.fuse.qa.fafram8.exceptions.VerifyFalseException;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;

import java.io.IOException;
import java.io.InputStream;

import lombok.extern.slf4j.Slf4j;

/**
 * Specific SSHClient for connecting to Fuse.
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
@Slf4j
public class FuseSSHClient extends SSHClient {

	@Override
	public void connect(boolean supressLog) throws VerifyFalseException, SSHClientException {
		final int sessionTimeout = 20000;
		try {
			if (!"none".equals(privateKey)) {
				if (passphrase != null) {
					ssh.addIdentity(privateKey, passphrase);
				} else {
					ssh.addIdentity(privateKey);
				}
			}

			session = ssh.getSession(username, hostname, port);

			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);

			session.connect(sessionTimeout);

			log.info("Connection established.");
		} catch (JSchException ex) {
			if (ex.getMessage().contains("verify false")) {
				if (!supressLog) {
					log.error("JschException caught - Verify false");
				}
				throw new VerifyFalseException(ex);
			}

			if (ex.getMessage().contains("timeout: socket is not established")) {
				log.error("Unable to connect to specified host: " + session.getHost() + ":" + session.getPort()
						+ " after " + sessionTimeout + " miliseconds");
				throw new SSHClientException("Unable to connect to specified host: " + session.getHost() + ":"
						+ session.getPort() + " after " + sessionTimeout + " miliseconds");
			}

			if (!supressLog) {
				log.error(ex.getLocalizedMessage());
			}
			throw new SSHClientException(ex);
		}
	}

	@Override
	public String executeCommand(String command, boolean supressLog) throws KarafSessionDownException,
			SSHClientException {
		log.info("Executing command: " + command);

		final int retriesCount = 2;
		final long commandRetryTimeout = 5000L;
		try {
			// If we should retry the command
			boolean retry;

			int retries = 0;

			String returnString = "";
			do {
				if (retries == retriesCount) {
					// If we retried it 2 times already, break
					break;
				}
				channel = session.openChannel("exec");
				((ChannelExec) channel).setCommand(command);

				channel.setInputStream(null);
				((ChannelExec) channel).setErrStream(System.err);

				final InputStream in = channel.getInputStream();

				channel.connect();

				returnString = convertStreamToString(in);
				if (returnString.contains("not found")) {
					log.debug("Retrying command in " + commandRetryTimeout + " miliseconds");
					retry = true;
					retries++;
					// Wait for 5 sec before executing command
					try {
						Thread.sleep(commandRetryTimeout);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					retry = false;
				}
			} while (retry);

			returnString = returnString.replaceAll("\u001B\\[[;\\d]*m", "").trim();
			log.debug("Command response: " + returnString);
			return returnString;
		} catch (JSchException ex) {
			if (ex.getMessage().contains("session is down")) {
				if (!supressLog) {
					log.error("JschException caught - Session is down");
				}
				throw new KarafSessionDownException(ex);
			}

			log.error("Cannot execute Fuse ssh command: \"" + command + "\"", ex);
			throw new SSHClientException(ex);
		} catch (IOException ex) {
			log.error(ex.getLocalizedMessage());
			throw new SSHClientException(ex);
		}
	}
}
