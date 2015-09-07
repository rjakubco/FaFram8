package org.jboss.fuse.qa.fafram8.deployer;

import org.jboss.fuse.qa.fafram8.exceptions.SSHClientException;
import org.jboss.fuse.qa.fafram8.manager.NodeManager;
import org.jboss.fuse.qa.fafram8.manager.RemoteNodeManager;
import org.jboss.fuse.qa.fafram8.ssh.SSHClient;

/**
 * Remote deployer class.
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RemoteDeployer implements Deployer {
	private RemoteNodeManager nm;
	// TODO add container manager with assigned FuseNodeClient

	/**
	 * Constructor
	 *
	 * @param nodeClient sshClient to remote host
	 * @param fuseClient sshClient to fuse on remote host
	 * @throws SSHClientException if there is some serious problem with ssh
	 */
	public RemoteDeployer(SSHClient nodeClient, SSHClient fuseClient) throws SSHClientException {
		this.nm = new RemoteNodeManager(nodeClient, fuseClient);
	}

	@Override
	public void setup() {
		// TODO add clean and only connect options for manipulating the test
		nm.stopAndClean();
		nm.prepareZip();
		nm.unzipArtifact();
		nm.prepareFuse();
		nm.startFuse();
	}

	@Override
	public void tearDown() {
		// TODO nothing to do ? ?? clean system properties
	}

	@Override
	public NodeManager getNodeManager() {
		return nm;
	}
}
