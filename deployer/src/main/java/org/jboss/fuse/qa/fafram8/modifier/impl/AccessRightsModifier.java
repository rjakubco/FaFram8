package org.jboss.fuse.qa.fafram8.modifier.impl;

import org.jboss.fuse.qa.fafram8.modifier.Modifier;
import org.jboss.fuse.qa.fafram8.property.FaframConstant;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * Access rights modifier.
 * Created by avano on 20.8.15.
 */
@AllArgsConstructor
@ToString
public class AccessRightsModifier implements Modifier {
	@SuppressWarnings("MismatchedReadAndWriteOfArray")
	private String[] paths;

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Override
	public void execute() {
		for (String path : paths) {
			new File(System.getProperty(FaframConstant.FUSE_PATH) + File.separator + path).setExecutable(true);
		}
	}

	/**
	 * Factory method - command for changing access rights.
	 *
	 * @param paths array of paths
	 * @return command instance
	 */
	public static AccessRightsModifier setExecutable(String... paths) {
		return new AccessRightsModifier(paths);
	}
}
