package org.jboss.fuse.qa.fafram8.exception;

/**
 * Created by ecervena on 9.10.15.
 */
public class InvokerPoolInterruptedException extends RuntimeException {
	/**
	 * Constructor.
	 */
	public InvokerPoolInterruptedException() {
	}

	/**
	 * Constructor.
	 *
	 * @param message message
	 */
	public InvokerPoolInterruptedException(String message) {
		super(message);
	}
}
