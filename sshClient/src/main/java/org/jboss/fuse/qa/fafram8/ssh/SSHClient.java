package org.jboss.fuse.qa.fafram8.ssh;

import org.apache.commons.io.IOUtils;

import org.jboss.fuse.qa.fafram8.exceptions.KarafSessionDownException;
import org.jboss.fuse.qa.fafram8.exceptions.SSHClientException;
import org.jboss.fuse.qa.fafram8.exceptions.VerifyFalseException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.IOException;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract class for SSHClients.
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public abstract class SSHClient {

	@Getter
	@Setter
	protected String hostname = "localhost";

	@Getter
	@Setter
	protected int port = -1;

	@Getter
	@Setter
	protected String username;

	@Getter
	@Setter
	protected String password;

	@Getter
	@Setter
	protected String privateKey = "none";

	@Getter
	@Setter
	protected String passphrase;

	@Getter
	@Setter
	protected Session session;

	@Getter
	@Setter
	protected Channel channel;

	protected JSch ssh = new JSch();

	/**
	 * Method for executing command on connected SSH server. Each implementation has some specific small hacks
	 *
	 * @param command command to be executed
	 * @param supressLog supress exception logging
	 * @return String containing response from command
	 * @throws KarafSessionDownException throws this exception if Karaf is down(specific for FuseSSHClient)
	 * @throws SSHClientException common exception for sshclient when there is some problem in connecting
	 * 							 (auth fail, timeout, wrong host/port)
	 */
	public abstract String executeCommand(String command, boolean supressLog) throws KarafSessionDownException,
			SSHClientException, InterruptedException;

	/**
	 * Method for creating connection and session, that is is used in executeCommand() method.
	 *
	 * @param supressLog supress exception logging
	 * @throws VerifyFalseException throw this exception when JschClient drop connection
	 * @throws SSHClientException common exception for sshclient when there is some problem in executing command
	 */
	public abstract void connect(boolean supressLog) throws VerifyFalseException, SSHClientException;

	/**
	 * Disconnects channel and session.
	 */
	public void disconnect() {

		if (channel != null) {
			channel.disconnect();
		}
		if (session != null) {
			session.disconnect();
		}
	}

	/**
	 * Helper method for checking if session is connected.
	 *
	 * @return true if connected
	 */
	public Boolean isConnected() {
		return session != null && session.isConnected();
	}

	/**
	 * Helper method for converting Stream to String.
	 *
	 * @param is InputStream to be converted to String
	 * @return crated String from InputStream
	 * @throws IOException if there is some problem with conversion
	 */
	protected String convertStreamToString(java.io.InputStream is) throws IOException {
		return IOUtils.toString(is, "UTF-8");
	}

	public SSHClient hostname(String host) {
		this.hostname = host;
		return this;
	}

	public SSHClient port(int port) {
		this.port = port;
		return this;
	}

	public SSHClient username(String username) {
		this.username = username;
		return this;
	}

	public SSHClient password(String password) {
		this.password = password;
		return this;
	}

	public SSHClient privateKey(String privateKey) {
		this.privateKey = privateKey;
		return this;
	}

	public SSHClient passphrase(String passphrase) {
		this.passphrase = passphrase;
		return this;
	}

	public SSHClient defaultSSHPort() {
		this.port = 22;
		return this;
	}

	public SSHClient fuseSSHPort() {
		this.port = 8101;
		return this;
	}
}
