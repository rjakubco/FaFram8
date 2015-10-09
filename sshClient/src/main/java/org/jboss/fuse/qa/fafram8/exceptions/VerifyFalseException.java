package org.jboss.fuse.qa.fafram8.exceptions;

/**
 * Exception for wrapping JschException: verify false for better handling of ssh connection to Fuse or Node.
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class VerifyFalseException extends Exception {
	/**
	 * Constructor.
	 */
	public VerifyFalseException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param cause cause
	 */
	public VerifyFalseException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 *
	 * @param message message
	 */
	public VerifyFalseException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param message message
	 * @param cause cause
	 */
	public VerifyFalseException(String message, Throwable cause) {
		super(message, cause);
	}
}
