package org.jboss.fuse.qa.fafram8.exceptions;

/**
 * Exception for wrapping JschException: session is down for better handling of ssh connection to Fuse.
 *
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
}
