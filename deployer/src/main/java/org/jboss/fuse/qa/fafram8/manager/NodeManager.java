package org.jboss.fuse.qa.fafram8.manager;

import org.jboss.fuse.qa.fafram8.watcher.Executor;

/**
 * Node manager interface.
 * Created by avano on 19.8.15.
 */
public interface NodeManager {
	void prepareZip();

	void unzipArtifact();

	void prepareFuse();

	void startFuse();

	Executor getExecutor();
}
