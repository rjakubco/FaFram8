package org.jboss.fuse.qa.fafram8.exception;

/**
 * Validator exception class.
 * Created by avano on 22.9.15.
 */
public class ValidatorException extends RuntimeException {
	/**
	 * Constructor.
	 */
	public ValidatorException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param message message
	 */
	public ValidatorException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param cause cause
	 */
	public ValidatorException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 *
	 * @param message message
	 * @param cause cause
	 */
	public ValidatorException(String message, Throwable cause) {
		super(message, cause);
	}
}
