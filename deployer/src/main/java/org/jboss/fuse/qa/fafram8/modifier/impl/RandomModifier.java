package org.jboss.fuse.qa.fafram8.modifier.impl;

import org.apache.commons.io.IOUtils;

import org.jboss.fuse.qa.fafram8.modifier.Modifier;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by avano on 16.9.15.
 */
@Slf4j
@ToString
public final class RandomModifier implements Modifier {

	/**
	 * Private constructor.
	 */
	private RandomModifier() {
	}

	@Override
	public void execute() {
		try {
			final String filePath = SystemProperty.getFusePath() + File.separator + "bin" + File.separator + "karaf";
			final FileInputStream fis = new FileInputStream(filePath);
			String content = IOUtils.toString(fis);
			content =
					content.replaceAll("exec \"\\$JAVA\"", "exec \"\\$JAVA\" -Djava.security.egd=file:/dev/./urandom");
			final FileOutputStream fos = new FileOutputStream(filePath);
			IOUtils.write(content, fos);

			fis.close();
			fos.close();
		} catch (Exception ex) {
			log.error("Error while manipulating the files " + ex);
		}
	}

	/**
	 * Factory method.
	 * @return random modifier instance
	 */
	public static RandomModifier applyOpenstackFix() {
		return new RandomModifier();
	}
}
