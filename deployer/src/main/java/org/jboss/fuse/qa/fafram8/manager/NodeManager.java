package org.jboss.fuse.qa.fafram8.manager;

import org.jboss.fuse.qa.fafram8.executor.Executor;

/**
 * Node manager interface.
 * Created by avano on 19.8.15.
 */
public interface NodeManager {
	/**
	 * Prepares the zip file.
	 */
	void prepareZip();

	/**
	 * Unzips the zip file.
	 */
	void unzipArtifact();

	/**
	 * Configures fuse.
	 */
	void prepareFuse();

	/**
	 * Starts fuse.
	 */
	void startFuse();

	/**
	 * Gets the executor.
	 *
	 * @return executor
	 */
	Executor getExecutor();

	/**
	 * Adds user.
	 *
	 * @param user user
	 * @param password password
	 * @param roles comma-separated roles
	 */
	void addUser(String user, String password, String roles);

	/**
	 * Adds property to the specified file.
	 *
	 * @param filePath path to the file relative to fuse home
	 * @param key key
	 * @param value value
	 */
	void addProperty(String filePath, String key, String value);

	/**
	 * Replaces the file.
	 *
	 * @param fileToReplace file to replace
	 * @param fileToUse file to use
	 */
	void replaceFile(String fileToReplace, String fileToUse);

	/**
	 * Stops the container and cleans the workspace if necessary.
	 */
	void stopAndClean();
}
