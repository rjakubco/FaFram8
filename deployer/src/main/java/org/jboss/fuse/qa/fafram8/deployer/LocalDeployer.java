package org.jboss.fuse.qa.fafram8.deployer;

import org.jboss.fuse.qa.fafram8.exception.FaframException;
import org.jboss.fuse.qa.fafram8.manager.ContainerManager;
import org.jboss.fuse.qa.fafram8.manager.LocalNodeManager;
import org.jboss.fuse.qa.fafram8.manager.NodeManager;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;
import org.jboss.fuse.qa.fafram8.ssh.SSHClient;

/**
 * Local deployer class.
 * Created by avano on 19.8.15.
 */
public class LocalDeployer implements Deployer {
	private LocalNodeManager nm;
	private ContainerManager cm;

	/**
	 * Constructor.
	 *
	 * @param client ssh client instance
	 */
	public LocalDeployer(SSHClient client) {
		nm = new LocalNodeManager(client);
		cm = new ContainerManager(client);
	}

	/**
	 * Setup FUSE root container on localhost.
	 */
	@Override
	public void setup() {
		try {
			nm.checkRunningContainer();
			nm.detectPlatformAndProduct();
			nm.prepareZip();
			nm.unzipArtifact();
			nm.prepareFuse();
			if (!SystemProperty.suppressStart()) {
				nm.startFuse();
				cm.patchStandaloneBeforeFabric();
				if (SystemProperty.isFabric()) {
					cm.setupFabric();
				}
				cm.patchFuse();
			}
		} catch (RuntimeException ex) {
			nm.stopAndClean(true);
			throw new FaframException(ex);
		}
	}

	@Override
	public void tearDown() {
		nm.stopAndClean(false);
	}

	@Override
	public NodeManager getNodeManager() {
		return nm;
	}

	@Override
	public ContainerManager getContainerManager() {
		return cm;
	}
}
