package org.jboss.fuse.qa.fafram8.deployer;

import org.jboss.fuse.qa.fafram8.manager.NodeManager;

/**
 * Deployer interface.
 * Created by avano on 19.8.15.
 */
public interface Deployer {
	void setup();

	void tearDown();

	NodeManager getNodeManager();
}
