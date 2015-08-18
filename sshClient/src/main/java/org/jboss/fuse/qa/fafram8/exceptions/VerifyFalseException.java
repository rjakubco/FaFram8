package org.jboss.fuse.qa.fafram8.exceptions;

/**
 * Exception for wrapping JschException: verify false for better handling of ssh connection to Fuse or Node
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class VerifyFalseException extends Exception {
	public VerifyFalseException() {
		super();
	}

	public VerifyFalseException(Throwable cause) {
		super(cause);
	}

	public VerifyFalseException(String message) {
		super(message);
	}

	public VerifyFalseException(String message, Throwable cause) {
		super(message, cause);
	}

	protected VerifyFalseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
