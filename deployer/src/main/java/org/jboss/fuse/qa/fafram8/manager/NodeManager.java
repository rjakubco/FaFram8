package org.jboss.fuse.qa.fafram8.manager;

import org.jboss.fuse.qa.fafram8.executor.Executor;

/**
 * Node manager interface.
 * Created by avano on 19.8.15.
 */
public interface NodeManager {
	void prepareZip();

	void unzipArtifact();

	void prepareFuse();

	void startFuse();

	void addUser(String user, String password, String roles);

	void replaceFile(String fileToReplace, String fileToUse);

	Executor getExecutor();
}
