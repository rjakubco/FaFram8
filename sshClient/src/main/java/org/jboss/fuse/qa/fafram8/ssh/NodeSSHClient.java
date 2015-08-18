package org.jboss.fuse.qa.fafram8.ssh;

import org.jboss.fuse.qa.fafram8.exceptions.CopyFileException;
import org.jboss.fuse.qa.fafram8.exceptions.KarafSessionDownException;
import org.jboss.fuse.qa.fafram8.exceptions.SSHClientException;
import org.jboss.fuse.qa.fafram8.exceptions.VerifyFalseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Specific SSHClient for connecting to Node
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class NodeSSHClient extends AbstractSSHClient {
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

			logger.debug("get session");
			session = ssh.getSession(username, hostname, port);

			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);

			logger.debug("session connecting");
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

			logger.debug("open channel");
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();

			logger.debug("channel connecting");
			channel.connect();

			returnString = convertStreamToString(in);

			return returnString;
		} catch (JSchException ex) {
			logger.error("Cannot execute ssh command", ex);
			throw new SSHClientException(ex);
		} catch (IOException ex) {
			logger.error(ex.getLocalizedMessage());
			throw new SSHClientException(ex);
		}
	}

	/**
	 * Copies a file to the remote machine.
	 *
	 * @param localPath local path to file
	 * @param remotePath remote path to file
	 * @throws CopyFileException if there was problem with copying the file
	 */
	public void copyFileToRemote(String localPath, String remotePath) throws CopyFileException {
		logger.info("Copying file " + localPath + " to remote machine path " + remotePath);

		try {
			ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
			sftpChannel.connect();
			File file = new File(localPath);
			sftpChannel.cd(remotePath);
			sftpChannel.put(new FileInputStream(file), file.getName());
			sftpChannel.disconnect();
		} catch (Exception ex) {
			logger.error("Exception thrown during uploading file to remote machine");
			throw new CopyFileException(ex);
		}
	}
}
