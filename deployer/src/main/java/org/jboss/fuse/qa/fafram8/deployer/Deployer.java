package org.jboss.fuse.qa.fafram8.deployer;

import org.jboss.fuse.qa.fafram8.manager.ContainerManager;
import org.jboss.fuse.qa.fafram8.manager.NodeManager;

/**
 * Deployer interface.
 * Created by avano on 19.8.15.
 */
public interface Deployer {
	/**
	 * Setup method.
	 */
	void setup();

	/**
	 * Teardown method.
	 */
	void tearDown();

	/**
	 * Node manager getter.
	 * @return node manager instance
	 */
	NodeManager getNodeManager();

	/**
	 * Container manager getter.
	 * @return container manager instance
	 */
	ContainerManager getContainerManager();
}
