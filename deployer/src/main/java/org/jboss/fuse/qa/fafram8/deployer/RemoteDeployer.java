package org.jboss.fuse.qa.fafram8.deployer;

import org.jboss.fuse.qa.fafram8.ConfigParser.ConfigurationParser;
import org.jboss.fuse.qa.fafram8.exception.FaframException;
import org.jboss.fuse.qa.fafram8.exceptions.SSHClientException;
import org.jboss.fuse.qa.fafram8.manager.ContainerManager;
import org.jboss.fuse.qa.fafram8.manager.NodeManager;
import org.jboss.fuse.qa.fafram8.manager.RemoteNodeManager;
import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.ssh.SSHClient;

/**
 * Remote deployer class.
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RemoteDeployer implements Deployer {
	private RemoteNodeManager nm;
	private ContainerManager cm;
	private ConfigurationParser configurationParser;

	/**
	 * Constructor.
	 *
	 * @param nodeClient sshClient to remote host
	 * @param fuseClient sshClient to fuse on remote host
	 * @throws SSHClientException if there is some serious problem with ssh
	 */
	public RemoteDeployer(SSHClient nodeClient, SSHClient fuseClient) throws SSHClientException {
		this.nm = new RemoteNodeManager(nodeClient, fuseClient);
		this.cm = new ContainerManager(fuseClient);
		this.configurationParser = new ConfigurationParser();
		this.configurationParser.parseConfigurationFile("path/to/configuration/file");
	}

	@Override
	public void setup() {
		// TODO add clean and only connect options for manipulating the test
		try {
			nm.stopAndClean();
			nm.prepareZip();
			nm.unzipArtifact();
			nm.prepareFuse();
			nm.startFuse();
			if (System.getProperty(FaframConstant.FABRIC) != null) {
				cm.setupFabric();
				// TODO rework this when we will have the container parser
				cm.createSSHContainer(configurationParser.getContainerList());
			}
		} catch (RuntimeException ex) {
			nm.stopAndClean();
			throw new FaframException(ex);
		}
	}

	@Override
	public void tearDown() {
		// TODO nothing to do ? ?? clean system properties
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
