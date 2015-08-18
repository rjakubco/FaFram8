package org.jboss.fuse.qa.fafram8.exceptions;

/**
 * Exception used for wrapping jsch exception to let user know that there was problem with copying file
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class CopyFileException extends Exception {
	public CopyFileException() {
		super();
	}

	public CopyFileException(Throwable cause) {
		super(cause);
	}

	public CopyFileException(String message) {
		super(message);
	}

	public CopyFileException(String message, Throwable cause) {
		super(message, cause);
	}

	protected CopyFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
