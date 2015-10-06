package org.jboss.fuse.qa.fafram8.modifier.impl;

import org.jboss.fuse.qa.fafram8.executor.Executor;
import org.jboss.fuse.qa.fafram8.modifier.Modifier;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;

import java.io.File;

import lombok.Setter;
import lombok.ToString;

/**
 * Access rights modifier.
 * Created by avano on 20.8.15.
 */
@ToString
public final class AccessRightsModifier implements Modifier {
	@Setter
	private Executor executor;

	@SuppressWarnings("MismatchedReadAndWriteOfArray")
	private String[] paths;

	/**
	 * Private constructor.
	 *
	 * @param paths paths
	 */
	private AccessRightsModifier(String[] paths) {
		this.paths = paths;
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Override
	public void execute() {
		for (String path : paths) {
			new File(SystemProperty.getFusePath() + File.separator + path).setExecutable(true);
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
