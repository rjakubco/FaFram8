package org.jboss.fuse.qa.fafram8.manager;

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

	public ContainerManager(SSHClient client) {
		this.executor = new Executor(client);
	}

	public void patchFuse() {
		if (System.getProperty("fabric") == null) {
			patchStandalone();
		} else {
			patchFabric();
		}
	}

	private void patchStandalone() {
		for (String s : Patcher.getPatches()) {
			String patchName = getPatchName(executor.executeCommand("patch:add " + s));
			executor.executeCommand("patch:install " + patchName);
			executor.waitForPatch(patchName);
		}
	}

	private void patchFabric() {
		// Create a new version
		executor.executeCommand("version-create patch");

		for (String s : Patcher.getPatches()) {
			executor.executeCommand("patch-apply -u " + SystemProperty.FUSE_USER + " -p " + SystemProperty
					.FUSE_PASSWORD + " --version patch " + s);
		}

		executor.executeCommand("container-upgrade root patch");
		executor.waitForProvisioning("root");
		executor.executeCommand("version-set-default patch");
	}

	/**
	 * Gets the patch name from the patch-add response.
	 *
	 * @param patchAddResponse patch-add command response.
	 * @return patch name
	 */
	private String getPatchName(String patchAddResponse) {
		// Get the 2nd row only
		String response = patchAddResponse.substring(patchAddResponse.indexOf(System.lineSeparator()) + 1);

		// Replace multiple whitespaces
		response = response.replaceAll(" +", " ").trim();

		// Get the first string in this line
		String patchName = response.split(" ")[0];
		log.debug("Patch name is " + patchName);
		return patchName;
	}
}
