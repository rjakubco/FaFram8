package org.jboss.fuse.qa.fafram8.exception;

/**
 * Created by ecervena on 9.10.15.
 */
public class InvokerPoolInteruptedException extends RuntimeException {
	/**
	 * Constructor.
	 */
	public InvokerPoolInteruptedException() {
	}

	/**
	 * Constructor.
	 *
	 * @param message message
	 */
	public InvokerPoolInteruptedException(String message) {
		super(message);
	}
}
