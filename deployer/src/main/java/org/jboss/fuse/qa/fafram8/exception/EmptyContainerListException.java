package org.jboss.fuse.qa.fafram8.exception;

/**
 * Empty container list exception class.
 * Created by ecervena on 9/8/15.
 */
public class EmptyContainerListException extends RuntimeException {

	/**
	 * Constructor.
	 */
	public EmptyContainerListException() {
	}

	/**
	 * Constructor.
	 *
	 * @param message message
	 */
	public EmptyContainerListException(String message) {
		super(message);
	}
}
