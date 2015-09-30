package org.jboss.fuse.qa.fafram8.exception;

/**
 * Created by ecervena on 24.9.15.
 */
public class UniqueNodeNameException extends RuntimeException {
	/**
	 * Constructor.
	 */
	public UniqueNodeNameException() {
	}

	/**
	 * Constructor.
	 *
	 * @param message message
	 */
	public UniqueNodeNameException(String message) {
		super(message);
	}
}
