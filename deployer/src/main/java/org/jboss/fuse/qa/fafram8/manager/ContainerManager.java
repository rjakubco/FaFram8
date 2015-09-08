package org.jboss.fuse.qa.fafram8.manager;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import org.jboss.fuse.qa.fafram8.executor.Executor;
import org.jboss.fuse.qa.fafram8.patcher.Patcher;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;
import org.jboss.fuse.qa.fafram8.ssh.SSHClient;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Container manager class.
 * Created by avano on 2.9.15.
 */
@Slf4j
public class ContainerManager {
	@Getter
	private Executor executor;

	// Setup fabric?
	@Setter
	@Getter
	private boolean fabric = false;

	public ContainerManager(SSHClient client) {
		this.executor = new Executor(client);
	}

	/**
	 * Sets up fabric.
	 */
	public void setupFabric() {
		executor.executeCommand("fabric:create");
		try {
			executor.waitForProvisioning("root");
		} catch (RuntimeException ex) {
			// Container is not provisioned in time
			throw new RuntimeException("Container did not provision in time");
		}

		// Set system property to indicate that we are working with fabric
		System.setProperty("fabric", "");
	}

	/**
	 * Patch fuse.
	 */
	public void patchFuse() {
		if (System.getProperty("fabric") == null) {
			patchStandalone();
		} else {
			patchFabric();
		}
	}

	/**
	 * Patch standalone container.
	 */
	private void patchStandalone() {
		for (String s : Patcher.getPatches()) {
			String patchName = getPatchName(executor.executeCommand("patch:add " + s));
			executor.executeCommand("patch:install " + patchName);
			executor.waitForPatch(patchName);
		}
	}

	/**
	 * Patch fabric.
	 */
	private void patchFabric() {
		// Create a new version
		String version = executor.executeCommand("version-create").split(" ")[2];

		for (String s : Patcher.getPatches()) {
			executor.executeCommand("patch-apply -u " + SystemProperty.FUSE_USER + " -p " + SystemProperty
					.FUSE_PASSWORD + " --version " + version + " " + s);
		}

		executor.executeCommand("container-upgrade " + version + " root");
		executor.waitForProvisioning("root");
		executor.executeCommand("version-set-default " + version);
	}

	/**
	 * Gets the patch name from the patch-add response.
	 *
	 * @param patchAddResponse patch-add command response.
	 * @return patch name
	 */
	private String getPatchName(String patchAddResponse) {
		// Get the 2nd row only
		String response = StringUtils.substringAfter(patchAddResponse, System.lineSeparator());
		// Replace multiple whitespaces
		response = response.replaceAll(" +", " ").trim();

		// Get the first string in this line
		String patchName = response.split(" ")[0];
		log.debug("Patch name is " + patchName);
		return patchName;
	}

	private void createSSHContainer(String nodeIP, String containerName) {
		String command = String.format("container-create-ssh --host %s --user %s --password %s --resolver %s %s",
				nodeIP, SystemProperty.FUSE_USER, SystemProperty.FUSE_PASSWORD, "localip", containerName);
		executor.executeCommand(command);
		executor.waitForProvisioning(containerName);
	}

/*	private void createSSHContainer(List<Container> containerList) {
		for(Container containerList: container) {
			createSSHContainer(container.getNodeIP, container.getName);
		}
	}*/
}
