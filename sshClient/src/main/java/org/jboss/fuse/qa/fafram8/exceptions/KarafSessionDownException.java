package org.jboss.fuse.qa.fafram8.exceptions;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class KarafSessionDownException extends Exception {
	public KarafSessionDownException() {
		super();
	}

	public KarafSessionDownException(Throwable cause) {
		super(cause);
	}

	public KarafSessionDownException(String message) {
		super(message);
	}

	public KarafSessionDownException(String message, Throwable cause) {
		super(message, cause);
	}

	protected KarafSessionDownException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
