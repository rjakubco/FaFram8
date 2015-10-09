package org.jboss.fuse.qa.fafram8.exceptions;

/**
 * Exception used for wrapping jsch exception to let user know that there was problem with copying file.
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class CopyFileException extends Exception {
	/**
	 * Constructor.
	 */
	public CopyFileException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param cause cause
	 */
	public CopyFileException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 *
	 * @param message message
	 */
	public CopyFileException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param message message
	 * @param cause cause
	 */
	public CopyFileException(String message, Throwable cause) {
		super(message, cause);
	}
}
