package org.jboss.fuse.qa.fafram8.modifier.impl;

import org.apache.commons.io.FileUtils;

import org.jboss.fuse.qa.fafram8.modifier.Modifier;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;

import java.io.File;

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

	private FileModifier(String fileToReplace, String fileToUse) {
		this.fileToReplace = fileToReplace;
		this.fileToUse = fileToUse;
	}

	@Override
	public void execute() {
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
			throw new RuntimeException(
					"Copy from " + newFile.getAbsolutePath() + " to " + oldFile.getAbsolutePath()
							+ " failed! " + ex);
		}
	}

	/**
	 * Factory method - command for replacing files.
	 *
	 * @param fileToReplace old file to replace
	 * @param fileToUse new file to replace the old file with
	 * @return replace file command
	 */
	public static FileModifier moveFile(final String fileToReplace, final String fileToUse) {
		return new FileModifier(fileToReplace, fileToUse);
	}
}
