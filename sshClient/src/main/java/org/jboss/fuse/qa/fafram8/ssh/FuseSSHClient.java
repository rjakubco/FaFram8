package org.jboss.fuse.qa.fafram8.ssh;

import org.jboss.fuse.qa.fafram8.exceptions.KarafSessionDownException;
import org.jboss.fuse.qa.fafram8.exceptions.SSHClientException;
import org.jboss.fuse.qa.fafram8.exceptions.VerifyFalseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Specific SSHClient for connecting to Fuse
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class FuseSSHClient extends AbstractSSHClient {
	private static final Logger logger = LoggerFactory.getLogger(NodeSSHClient.class);

	@Override
	public void connect() throws VerifyFalseException, SSHClientException {
		try {
			if (!privateKey.equals("none")) {
				if (passphrase != null) {
					ssh.addIdentity(privateKey, passphrase);
				} else {
					ssh.addIdentity(privateKey);
				}
			}

			session = ssh.getSession(username, hostname, port);

			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);

			session.connect(20000);

			logger.info("Connection established.");
		} catch (JSchException ex) {
			if (ex.getMessage().contains("verify false")) {
				logger.debug("Verify false exception -> not important");
				throw new VerifyFalseException(ex);
			}

			if (ex.getMessage().contains("timeout: socket is not established")) {
				logger.error("Unable to connect to specified host: " + session.getHost() + ":" + session.getPort() + " in 20 seconds");
				throw new SSHClientException("Unable to connect to specified host: " + session.getHost() + ":" + session.getPort() + " in 20 seconds");
			}

			logger.error(ex.getLocalizedMessage());
			throw new SSHClientException(ex);
		}
	}

	@Override
	public String executeCommand(String command) throws KarafSessionDownException, SSHClientException {
		String returnString;

		logger.debug("Command: " + command);

		try {

			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();

			channel.connect();

			returnString = convertStreamToString(in);

			return returnString;
		} catch (JSchException ex) {
			if (ex.getMessage().contains("session is down")) {
				logger.debug("Session is down exception when starting Fuse or creating fabric -> not important");
				throw new KarafSessionDownException(ex);
			}

			logger.error("Cannot execute Fuse ssh command", ex);
			throw new SSHClientException(ex);
		} catch (IOException ex) {
			logger.error(ex.getLocalizedMessage());
			throw new SSHClientException(ex);
		}
	}
}
