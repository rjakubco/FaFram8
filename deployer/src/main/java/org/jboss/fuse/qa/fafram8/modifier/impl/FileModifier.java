package org.jboss.fuse.qa.fafram8.modifier.impl;

import org.apache.commons.io.FileUtils;

import org.jboss.fuse.qa.fafram8.exception.FaframException;
import org.jboss.fuse.qa.fafram8.exceptions.CopyFileException;
import org.jboss.fuse.qa.fafram8.executor.Executor;
import org.jboss.fuse.qa.fafram8.modifier.Modifier;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;

import java.io.File;

import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * File modifier class.
 * Created by avano on 20.8.15.
 */
@Slf4j
@ToString
public final class FileModifier implements Modifier {
	private String fileToReplace;
	private String fileToUse;
	@Setter
	private Executor executor;

	private FileModifier(String fileToReplace, String fileToUse, Executor executor) {
		this.fileToReplace = fileToReplace;
		this.fileToUse = fileToUse;
		this.executor = executor;
	}

	/**
	 * Factory method - command for replacing files.
	 *
	 * @param fileToReplace old file to replace
	 * @param fileToUse new file to replace the old file with
	 * @return replace file command
	 */
	public static FileModifier moveFile(final String fileToReplace, final String fileToUse) {
		return new FileModifier(fileToReplace, fileToUse, null);
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
	public static FileModifier moveFile(final String fileToReplace, final String fileToUse, final Executor executor) {
		return new FileModifier(fileToReplace, fileToUse, executor);
	}

	@Override
	public void execute() {
		if (executor == null) {
			moveLocalFile();
		} else {
			moveRemoteFile();
		}
	}

	/**
	 * Moves files on localhost.
	 */
	private void moveLocalFile() {
		final String oldFilePath = SystemProperty.getFusePath() + File.separator + fileToReplace;
		try {
			FileUtils.forceDelete(new File(oldFilePath));
		} catch (Exception ex) {
			log.error("Delete of " + oldFilePath + " failed! " + ex);
		}

		final File newFile = new File(fileToUse);
		final File oldFile = new File(oldFilePath);

		try {
			FileUtils.copyFile(newFile, oldFile);
		} catch (Exception ex) {
			log.error("Copy from " + newFile.getAbsolutePath() + " to " + oldFile.getAbsolutePath()
					+ " failed! " + ex);
			throw new FaframException(
					"Copy from " + newFile.getAbsolutePath() + " to " + oldFile.getAbsolutePath()
							+ " failed! " + ex);
		}
	}

	/**
	 * Moves files on remote.
	 */
	private void moveRemoteFile() {
		final String path = SystemProperty.getFusePath() + File.separator + fileToReplace;

		try {
			log.debug("Copying file: {} to remote location: {}", fileToUse, path);
			executor.copyFileToRemote(fileToUse, path);
		} catch (CopyFileException ex) {
			log.error("Could not copy file to remote location: ", ex);
			throw new FaframException("Could not copy file to remote location: ", ex);
		}
	}
}
