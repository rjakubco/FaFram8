package org.jboss.fuse.qa.fafram8.resource;

import static org.jboss.fuse.qa.fafram8.modifier.impl.FileModifier.moveFile;
import static org.jboss.fuse.qa.fafram8.modifier.impl.JvmOptsModifier.setDefaultJvmOpts;
import static org.jboss.fuse.qa.fafram8.modifier.impl.JvmOptsModifier.setJvmOpts;
import static org.jboss.fuse.qa.fafram8.modifier.impl.PropertyModifier.extendProperty;
import static org.jboss.fuse.qa.fafram8.modifier.impl.PropertyModifier.putProperty;
import static org.jboss.fuse.qa.fafram8.modifier.impl.RandomModifier.changeRandomSource;

import org.jboss.fuse.qa.fafram8.configuration.ConfigurationParser;
import org.jboss.fuse.qa.fafram8.deployer.Deployer;
import org.jboss.fuse.qa.fafram8.deployer.LocalDeployer;
import org.jboss.fuse.qa.fafram8.deployer.RemoteDeployer;
import org.jboss.fuse.qa.fafram8.exceptions.SSHClientException;
import org.jboss.fuse.qa.fafram8.manager.Container;
import org.jboss.fuse.qa.fafram8.manager.LocalNodeManager;
import org.jboss.fuse.qa.fafram8.modifier.ModifierExecutor;
import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;
import org.jboss.fuse.qa.fafram8.provision.provider.ProvisionProvider;
import org.jboss.fuse.qa.fafram8.provision.provider.StaticProvider;
import org.jboss.fuse.qa.fafram8.ssh.FuseSSHClient;
import org.jboss.fuse.qa.fafram8.ssh.NodeSSHClient;
import org.jboss.fuse.qa.fafram8.ssh.SSHClient;
import org.jboss.fuse.qa.fafram8.validator.Validator;

import org.junit.rules.ExternalResource;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Fafram resource class.
 * Created by avano on 19.8.15.
 */
@Slf4j
public class Fafram extends ExternalResource {
	//List of containers used in test
	@Getter
	private static final List<Container> containerList = new LinkedList<>();
	//Provision provider instance in case of remote deployment
	@Getter
	private static ProvisionProvider provisionProvider = new StaticProvider();
	// Deployer instance
	private Deployer deployer;

	/**
	 * Constructor.
	 */
	public Fafram() {
	}

	/**
	 * Create Fafram and set ProvisionProvider implementation for remote deployment on-demmand provisioning.
	 *
	 * @param provisionProvider implementation of ProvisionProvider interface.
	 */
	public Fafram(ProvisionProvider provisionProvider) {
		this.provisionProvider = provisionProvider;
	}

	/**
	 * Adds container object model to container list.
	 *
	 * @param container container specification picked up from configuration file
	 */
	public static void addContainer(Container container) {
		containerList.add(container);
	}

	@Override
	protected void before() {
		setup();
	}

	@Override
	protected void after() {
		tearDown();
	}

	/**
	 * Start method.
	 *
	 * @return this
	 */
	public Fafram setup() {
		//TODO(all): consider entry point of configuration parser
		ConfigurationParser.parseConfigurationFile("just/fake/path");
		//uncoment for remote deployment
		//ConfigurationParser.setDeployer();

		if (SystemProperty.getHost() == null) {
			log.info("Setting up local deployment");
			Validator.validate();
			setupLocalDeployment();
		} else {
			prepareNodes(provisionProvider);
			Validator.validate();
			log.info("Setting up remote deployment on host " + SystemProperty.getHost() + ":" + SystemProperty
					.getHostPort());
			try {
				setupRemoteDeployment();
			} catch (SSHClientException e) {
				e.printStackTrace();
			}
		}

		setDefaultModifiers();

		// Start deployer
		deployer.setup();
		return this;
	}

	/**
	 * Stop method.
	 */
	public void tearDown() {
		// Do nothing if deployer is null - when the validation fails.
		if (deployer != null) {
			deployer.tearDown();
		}
	}

	/**
	 * Sets the default modifiers common for both local and remote deploy.
	 */
	private void setDefaultModifiers() {
		if (!SystemProperty.skipDefaultUser()) {
			// Add default user
			ModifierExecutor.addModifiers(putProperty("etc/users.properties", SystemProperty.getFuseUser(),
					SystemProperty.getFusePassword() + ",admin,manager,viewer,Monitor, Operator, Maintainer, Deployer, "
							+ "Auditor, Administrator, SuperUser"));
		}

		ModifierExecutor.addModifiers(changeRandomSource());

		if (!SystemProperty.skipDefaultJvmOpts()) {
			ModifierExecutor.addModifiers(setDefaultJvmOpts());
		}
	}

	/**
	 * Sets up the local deployment.
	 */
	private void setupLocalDeployment() {
		final int defaultPort = 8101;

		// Create a local deployer with local SSH Client and assign to deployer variable
		deployer = new LocalDeployer(new FuseSSHClient().hostname("localhost").port(defaultPort).username(SystemProperty
				.getFuseUser()).password(SystemProperty.getFusePassword()));
	}

	/**
	 * Sets up the remote deployment.
	 */
	private void setupRemoteDeployment() throws SSHClientException {
		// Use fabric by default on remote
		SystemProperty.set(FaframConstant.FABRIC, "");

		final SSHClient node = new NodeSSHClient().hostname(SystemProperty.getHost()).port(SystemProperty.getHostPort())
				.username(SystemProperty.getHostUser()).password(SystemProperty.getHostPassword());

		final SSHClient fuse = new FuseSSHClient().hostname(SystemProperty.getHost()).fuseSSHPort().username(
				SystemProperty.getFuseUser()).password(SystemProperty.getFusePassword());

		deployer = new RemoteDeployer(node, fuse);
	}

	/**
	 * Executes a command in node shell.
	 *
	 * @param command command
	 * @return command response
	 */
	public String executeNodeCommand(String command) {
		return deployer.getNodeManager().getExecutor().executeCommand(command);
	}

	/**
	 * Executes a command in root container shell.
	 *
	 * @param command command to execute on root container
	 * @return command response
	 */
	public String executeCommand(String command) {
		return deployer.getContainerManager().getExecutor().executeCommand(command);
	}

	/**
	 * Adds a new user.
	 *
	 * @param user user
	 * @param password password
	 * @param roles comma-separated roles
	 * @return this
	 */
	public Fafram addUser(String user, String password, String roles) {
		ModifierExecutor.addModifiers(putProperty("etc/users.properties", user, password + "," + roles));
		return this;
	}

	/**
	 * Modifies (add/extend) a property.
	 *
	 * @param file file path relative to karaf home
	 * @param key key
	 * @param value value
	 * @param extend extend flag
	 * @return this
	 */
	public Fafram modifyProperty(String file, String key, String value, boolean extend) {
		if (extend) {
			ModifierExecutor.addModifiers(putProperty(file, key, value));
		} else {
			ModifierExecutor.addModifiers(extendProperty(file, key, value));
		}

		return this;
	}

	/**
	 * Replaces a file.
	 *
	 * @param fileToReplace file to replace
	 * @param fileToUse file to use
	 * @return this
	 */
	public Fafram replaceFile(String fileToReplace, String fileToUse) {
		ModifierExecutor.addModifiers(moveFile(fileToReplace, fileToUse));
		return this;
	}

	/**
	 * Provide deployment with Fabric environment.
	 *
	 * @return this
	 */
	public Fafram withFabric() {
		return withFabric("");
	}

	/**
	 * Provide deployment with Fabric provision.
	 *
	 * @param opts fabric create options
	 * @return this
	 */
	public Fafram withFabric(String opts) {
		SystemProperty.set(FaframConstant.FABRIC, opts);
		return this;
	}

	/**
	 * Suppress the default user add.
	 *
	 * @return this
	 */
	public Fafram withoutDefaultUser() {
		SystemProperty.set(FaframConstant.SKIP_DEFAULT_USER, "");
		return this;
	}

	/**
	 * Patches standalone container. Only useful together with withFabric().
	 *
	 * @return this
	 */
	public Fafram patchStandalone() {
		SystemProperty.set(FaframConstant.PATCH_STANDALONE, "");
		return this;
	}

	/**
	 * Sets the JVM options.
	 *
	 * @param xms xms
	 * @param xmx xmx
	 * @param permMem perm mem
	 * @param maxPermMem max perm mem
	 * @return this
	 */
	public Fafram setJvmOptions(String xms, String xmx, String permMem, String maxPermMem) {
		ModifierExecutor.addModifiers(setJvmOpts(xms, xmx, permMem, maxPermMem));
		SystemProperty.set(FaframConstant.SKIP_DEFAULT_JVM_OPTS, "");
		return this;
	}

	/**
	 * Suppresses the start of the fuse - probably testing purposes only.
	 *
	 * @return this
	 */
	public Fafram suppressStart() {
		SystemProperty.set(FaframConstant.SUPPRESS_START, "");
		return this;
	}

	/**
	 * Restarts the container.
	 */
	public void restart() {
		// TODO(avano): probably won't be needed on remote
		((LocalNodeManager) deployer.getNodeManager()).restart();
	}

	/**
	 * Triggers specified provision provider. It will call providers functionality to provision pool of nodes required
	 * to satisfy needs of test deployment. Provider will create node for every container listed in Fafram.containerList.
	 * When the container is marked as root provider will assign public IP to it. Otherwise local IP will be provided.
	 * Provider have to implement ProvisionProvider interface.
	 *
	 * @param provider provider type name
	 */
	public void prepareNodes(ProvisionProvider provider) {
		provider.createServerPool(Fafram.containerList);
		provider.assignAddresses(Fafram.containerList);
	}

	/**
	 * Set ProvisionProvider implementation to Fafram.
	 *
	 * @param provider Implementation of ProvisionProvider interface
	 * @return this
	 */
	public Fafram provideNodes(ProvisionProvider provider) {
		provisionProvider = provider;
		return this;
	}
}
