package org.jboss.fuse.qa.fafram8.deployer;

import org.jboss.fuse.qa.fafram8.exception.FaframException;
import org.jboss.fuse.qa.fafram8.exceptions.SSHClientException;
import org.jboss.fuse.qa.fafram8.manager.ContainerManager;
import org.jboss.fuse.qa.fafram8.manager.NodeManager;
import org.jboss.fuse.qa.fafram8.manager.RemoteNodeManager;
import org.jboss.fuse.qa.fafram8.modifier.ModifierExecutor;
import org.jboss.fuse.qa.fafram8.property.FaframConstant;
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

		// because CLEAN property is by default null if not set in test/jenkins or change with onlyProperty() method on Fafram
		// then if is not set then set CLEAN property by default to true
		if (SystemProperty.getClean() == null) {
			System.setProperty(FaframConstant.CLEAN, "true");
		}
		//this.configurationParser = ConfigurationParser.getInstance();
		//TODO(ecervena): consider where parsing of config file should be called
		//this.configurationParser.parseConfigurationFile("path/to/configuration/file");
	}

	@Override
	public void setup() {
		try {
			// if clean do everything
			if (!SystemProperty.getClean()) {
				cm.getExecutor().connect();
				return;
			}

			nm.stop();
			nm.prepareZip();
			nm.unzipArtifact();
			nm.prepareFuse();
			if (!SystemProperty.suppressStart()) {
				nm.startFuse();
				cm.patchStandaloneBeforeFabric();
				if (SystemProperty.isFabric()) {
					cm.setupFabric();
					// TODO(ecervena): rework this when we will have the container parser
					// TODO(rjakubco): commented because it is not working at the moment and breaks other unrelated tests
//						cm.createSSHContainer(Fafram.getContainerList());
				}
			}
		} catch (RuntimeException ex) {
			if (SystemProperty.getClean()) {
				nm.stopAndClean(true);
			}
			throw new FaframException(ex);
		} catch (SSHClientException ex) {
			throw new FaframException(ex);
		}
	}

	@Override
	public void tearDown() {
		SystemProperty.clearAllProperties();
		ModifierExecutor.clearAllModifiers();
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
