package org.jboss.fuse.qa.fafram8.deployer;

import org.jboss.fuse.qa.fafram8.manager.ContainerManager;
import org.jboss.fuse.qa.fafram8.manager.LocalNodeManager;
import org.jboss.fuse.qa.fafram8.manager.NodeManager;
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
	 * @param client ssh client instance
	 */
	public LocalDeployer(SSHClient client) {
		nm = new LocalNodeManager(client);
		cm = new ContainerManager(client);
	}

	@Override
	public void setup() {
		nm.checkRunningContainer();
		nm.prepareZip();
		nm.unzipArtifact();
		nm.prepareFuse();
		nm.startFuse();
		cm.patchFuse();
	}

	@Override
	public void tearDown() {
		nm.stopAndClean();
	}

	@Override
	public NodeManager getNodeManager() {
		return nm;
	}
}
