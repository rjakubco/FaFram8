package org.jboss.fuse.qa.fafram8.exceptions;

/**
 * Common exception for wrapping important JschExceptions that are
 * not handled in special way (SessionDownEx, VerifyFalseEx).
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class SSHClientException extends Exception {
	/**
	 * Constructor.
	 */
	public SSHClientException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param cause cause
	 */
	public SSHClientException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 *
	 * @param message message
	 */
	public SSHClientException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param message message
	 * @param cause cause
	 */
	public SSHClientException(String message, Throwable cause) {
		super(message, cause);
	}
}
