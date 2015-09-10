package org.jboss.fuse.qa.fafram8.exception;

/**
 * Startup exception class.
 * Created by avano on 7.9.15.
 */
public class FaframException extends RuntimeException {
	public FaframException() {
		super();
	}

	public FaframException(String message) {
		super(message);
	}

	public FaframException(Throwable cause) {
		super(cause);
	}

	public FaframException(String message, Throwable cause) {
		super(message, cause);
	}
}
