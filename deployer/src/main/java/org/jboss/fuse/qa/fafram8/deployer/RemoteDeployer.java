package org.jboss.fuse.qa.fafram8.deployer;

import org.jboss.fuse.qa.fafram8.ConfigParser.ConfigurationParser;
import org.jboss.fuse.qa.fafram8.exception.FaframException;
import org.jboss.fuse.qa.fafram8.exceptions.SSHClientException;
import org.jboss.fuse.qa.fafram8.manager.ContainerManager;
import org.jboss.fuse.qa.fafram8.manager.NodeManager;
import org.jboss.fuse.qa.fafram8.manager.RemoteNodeManager;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;
import org.jboss.fuse.qa.fafram8.ssh.SSHClient;

/**
 * Remote deployer class.
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RemoteDeployer implements Deployer {
	private RemoteNodeManager nm;
	private ContainerManager cm;
	//private ConfigurationParser configurationParser;

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
		//this.configurationParser = ConfigurationParser.getInstance();
		//TODO(ecervena): consider where parsing of config file should be called
		//this.configurationParser.parseConfigurationFile("path/to/configuration/file");
	}

	@Override
	public void setup() {
		// TODO(rjakubco): add clean and only connect options for manipulating the test
		try {
			nm.stop();
			nm.prepareZip();
			nm.unzipArtifact();
			nm.prepareFuse();
			nm.startFuse();
			if (SystemProperty.isFabric()) {
				cm.setupFabric();
				// TODO(ecervena): rework this when we will have the container parser
				cm.createSSHContainer(ConfigurationParser.getContainerList());
			}
		} catch (RuntimeException ex) {
			nm.stopAndClean(true);
			throw new FaframException(ex);
		}
	}

	@Override
	public void tearDown() {
		// TODO(rjakubco): what to do here
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
