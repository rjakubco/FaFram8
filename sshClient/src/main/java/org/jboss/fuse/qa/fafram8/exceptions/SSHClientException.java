package org.jboss.fuse.qa.fafram8.exceptions;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class SSHClientException extends Exception {
	public SSHClientException() {
		super();
	}

	public SSHClientException(Throwable cause) {
		super(cause);
	}

	public SSHClientException(String message) {
		super(message);
	}

	public SSHClientException(String message, Throwable cause) {
		super(message, cause);
	}

	protected SSHClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
