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
 * Abstract class for SSHClients
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public abstract class AbstractSSHClient {

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
	 * @return String containing response from command
	 * @throws KarafSessionDownException throws this exception if Karaf is down(specific for FuseSSHClient)
	 * @throws SSHClientException common exception for sshclient when there is some problem in connecting (auth fail, timeout, wrong host/port)
	 */
	public abstract String executeCommand(String command) throws KarafSessionDownException, SSHClientException;

	/**
	 * Method for creating connection and session, that is is used in executeCommand() method
	 *
	 * @throws VerifyFalseException throw this exception when JschClient drop connection
	 * @throws SSHClientException common exception for sshclient when there is some problem in executing command
	 */
	public abstract void connect() throws VerifyFalseException, SSHClientException;

	/**
	 * Disconnects channel and session
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
	 * Helper method for converting Stream to String
	 *
	 * @param is InputStream to be converted to String
	 * @return crated String from InputStream
	 * @throws IOException if there is some problem with conversion
	 */
	protected String convertStreamToString(java.io.InputStream is) throws IOException {
		return IOUtils.toString(is, "UTF-8");
	}

	public AbstractSSHClient hostname(String host) {
		this.hostname = host;
		return this;
	}

	public AbstractSSHClient port(int port) {
		this.port = port;
		return this;
	}

	public AbstractSSHClient username(String username) {
		this.username = username;
		return this;
	}

	public AbstractSSHClient password(String password) {
		this.password = password;
		return this;
	}

	public AbstractSSHClient privateKey(String privateKey) {
		this.privateKey = privateKey;
		return this;
	}

	public AbstractSSHClient passphrase(String passphrase) {
		this.passphrase = passphrase;
		return this;
	}

	public AbstractSSHClient defaultSSHPort() {
		this.port = 22;
		return this;
	}

	public AbstractSSHClient fuseSSHPort() {
		this.port = 8101;
		return this;
	}
}
