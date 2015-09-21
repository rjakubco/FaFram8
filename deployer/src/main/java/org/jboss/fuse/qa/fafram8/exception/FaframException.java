package org.jboss.fuse.qa.fafram8.exception;

/**
 * Startup exception class.
 * Created by avano on 7.9.15.
 */
public class FaframException extends RuntimeException {
	/**
	 * Constructor.
	 */
	public FaframException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param message message
	 */
	public FaframException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param cause cause
	 */
	public FaframException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 *
	 * @param message message
	 * @param cause cause
	 */
	public FaframException(String message, Throwable cause) {
		super(message, cause);
	}
}
