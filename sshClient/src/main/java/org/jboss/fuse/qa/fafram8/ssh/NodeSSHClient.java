package org.jboss.fuse.qa.fafram8.ssh;

import org.jboss.fuse.qa.fafram8.exceptions.CopyFileException;
import org.jboss.fuse.qa.fafram8.exceptions.KarafSessionDownException;
import org.jboss.fuse.qa.fafram8.exceptions.SSHClientException;
import org.jboss.fuse.qa.fafram8.exceptions.VerifyFalseException;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.extern.slf4j.Slf4j;

/**
 * Specific SSHClient for connecting to Node.
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
@Slf4j
public class NodeSSHClient extends SSHClient {

	@Override
	public void connect(boolean supressLog) throws VerifyFalseException, SSHClientException {
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

			session.connect(20000);

			log.info("Connection established.");
		} catch (JSchException ex) {
			if (ex.getMessage().contains("verify false")) {
				if (!supressLog) {
					log.debug("JschException caught - Verify false");
				}
				throw new VerifyFalseException(ex);
			}

			if (ex.getMessage().contains("timeout: socket is not established")) {
				log.error("Unable to connect to specified host: " + session.getHost() + ":" + session.getPort()
						+ " after 20 seconds");
				throw new SSHClientException("Unable to connect to specified host: " + session.getHost() + ":"
						+ session.getPort() + " after 20 seconds");
			}

			log.error(ex.getLocalizedMessage());
			throw new SSHClientException(ex);
		}
	}

	@Override
	public String executeCommand(String command, boolean supressLog) throws KarafSessionDownException,
			SSHClientException {
		String returnString;

		log.debug("Command: " + command);

		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);

			final InputStream in = channel.getInputStream();

			channel.connect();

			returnString = convertStreamToString(in);

			returnString = returnString.replaceAll("\u001B\\[[;\\d]*m", "").trim();
			log.debug("Command response: " + returnString);
			return returnString;
		} catch (JSchException ex) {
			log.error("Cannot execute ssh command: \"" + command + "\"", ex);
			throw new SSHClientException(ex);
		} catch (IOException ex) {
			log.error(ex.getLocalizedMessage());
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
		log.info("Copying file " + localPath + " to remote machine path " + remotePath);

		try {
			final ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
			sftpChannel.connect();
			final File file = new File(localPath);
			sftpChannel.cd(remotePath);
			sftpChannel.put(new FileInputStream(file), file.getName());
			sftpChannel.disconnect();
		} catch (Exception ex) {
			log.error("Exception thrown during uploading file to remote machine");
			throw new CopyFileException(ex);
		}
	}
}
