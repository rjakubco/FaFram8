package org.jboss.fuse.qa.fafram8.modifier.impl;

import org.jboss.fuse.qa.fafram8.exceptions.CopyFileException;
import org.jboss.fuse.qa.fafram8.executor.Executor;
import org.jboss.fuse.qa.fafram8.modifier.Modifier;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;

import java.io.File;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Remote file modifier class.
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
@Slf4j
@ToString
public final class RemoteFileModifier implements Modifier {
	private String remoteFilePath;
	private String localFilePath;
	private Executor executor;

	private RemoteFileModifier(String fileToReplace, String fileToUse, Executor executor) {
		this.remoteFilePath = fileToReplace;
		this.localFilePath = fileToUse;
		this.executor = executor;
	}

	@Override
	public void execute() {
		final String path = SystemProperty.getFusePath() + File.separator + remoteFilePath;

		try {
			log.debug("Copying file: {} to remote location: {}", localFilePath, path);
			executor.copyFileToRemote(localFilePath, path);
		} catch (CopyFileException ex) {
			log.error("Could not copy file to remote location: ", ex);
			throw new RuntimeException("Could not copy file to remote location: ", ex);
		}
	}

	/**
	 * Static method for creating modifier that copies file to remote location.
	 *
	 * @param fileToReplace path to file inside Fuse folder that should be replaced or where the new file should
	 * be copied to
	 * @param fileToUse absolute path to local file that will be copied
	 * @param executor executor with ssh client to remote location
	 * @return RemoteFileModifier
	 */
	public static RemoteFileModifier moveRemoteFile(final String fileToReplace, final String fileToUse, final Executor executor) {
		return new RemoteFileModifier(fileToReplace, fileToUse, executor);
	}
}
