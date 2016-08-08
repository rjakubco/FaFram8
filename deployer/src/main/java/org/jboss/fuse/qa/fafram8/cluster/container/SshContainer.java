package org.jboss.fuse.qa.fafram8.cluster.container;

import org.jboss.fuse.qa.fafram8.cluster.node.Node;
import org.jboss.fuse.qa.fafram8.cluster.resolver.Resolver;
import org.jboss.fuse.qa.fafram8.deployer.ContainerSummoner;
import org.jboss.fuse.qa.fafram8.exception.FaframException;
import org.jboss.fuse.qa.fafram8.executor.Executor;
import org.jboss.fuse.qa.fafram8.manager.ContainerManager;
import org.jboss.fuse.qa.fafram8.property.FaframProvider;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;
import org.jboss.fuse.qa.fafram8.util.Option;
import org.jboss.fuse.qa.fafram8.util.OptionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * Class representing ssh container. Instances of this class should and can be created just with SshBuilder class.
 * Created by avano on 1.2.16.
 */
@Slf4j
public class SshContainer extends Container implements ThreadContainer {
	/**
	 * Constructor.
	 */
	protected SshContainer() {
	}

	/**
	 * Constructor.
	 *
	 * @param name container name
	 */
	protected SshContainer(String name) {
		super();
		super.setName(name);
		super.setRoot(false);
	}

	/**
	 * Builder getter.
	 *
	 * @return builder instance
	 */
	public static SshBuilder builder() {
		return new SshBuilder(null);
	}

	/**
	 * Builder getter.
	 *
	 * @param c container that will be copied
	 * @return builder instance
	 */
	public static SshBuilder builder(Container c) {
		return new SshBuilder(c);
	}

	@Override
	public void create() {
		create(super.getParent().getExecutor());
	}

	@Override
	public void create(Executor executor) {
		// This should be already done in almost all cases but left here for double checking -> adding containers in tests is probably only exception when parent
		// will be not set
		if (super.getParent() == null) {
			// Search the parent by its name
			final Container parent = ContainerManager.getContainer(super.getParentName());
			if (parent == null) {
				throw new FaframException(String.format("Specified parent (%s) of container %s does not exist in container list!",
						super.getParentName(), super.getName()));
			}
			super.setParent(parent);
		}
		if (SystemProperty.suppressStart()) {
			return;
		}

		// If using static provider then clean
		if (FaframProvider.STATIC.equals(SystemProperty.getProvider())) {
			clean();
		}

		if (!SystemProperty.getJavaHome().isEmpty()) {
			OptionUtils.set(super.getOptions(), Option.ENV, "JAVA_HOME=" + SystemProperty.getJavaHome());
		}

		// Find out if system property or parameter on container of working directory was set
		final String workDir = OptionUtils.getString(super.getOptions(), Option.WORKING_DIRECTORY);
		if (!("".equals(workDir)) || !("".equals(SystemProperty.getWorkingDirectory()))) {
			// Decide if working directory was set on ssh and if not set the system property as default
			final String path = "".equals(workDir) ? SystemProperty.getWorkingDirectory() : workDir;
			OptionUtils.set(super.getOptions(), Option.PATH, path);
			log.debug("Working directory was set. Setting working directory \"{}\" for container \"{}\".", path, super.getName());
		}

		log.info("Creating container " + this);
		executor.connect();
		// Recreate the executor because the values could be changed in the process
		super.getNode().setExecutor(super.getNode().createExecutor());

		executor.executeCommand(String.format("container-create-ssh --user %s --password %s --host %s %s %s",
				super.getNode().getUsername(), super.getNode().getPassword(), super.getNode().getHost(), OptionUtils.getCommand(super.getOptions()), super.getName()));
		super.setCreated(true);
		try {
			executor.waitForProvisioning(this);
		} catch (FaframException e) {
			ContainerSummoner.setStopWork(true);
			throw e;
		}

		super.setExecutor(super.createExecutor());
		super.getExecutor().connect();
		super.getNode().getExecutor().connect();
		super.setOnline(true);
		// Set the fuse path
		try {
			super.setFusePath(super.getExecutor().executeCommandSilently("shell:info | grep \"Karaf base\"").trim().replaceAll(" +", " ")
					.split(" ")[1]);
		} catch (Exception ex) {
			log.warn("Setting fuse path failed, it won't be available", ex);
		}
	}

	@Override
	public void destroy() {
		destroy(getExecutor());
	}

	@Override
	public void destroy(Executor executor) {
		if (SystemProperty.suppressStart() || !super.isCreated()) {
			return;
		}

		log.info("Destroying container " + super.getName());
		executor.connect();
		super.getParent().getExecutor().executeCommand("container-delete --force " + super.getName());
		super.setCreated(false);
		ContainerManager.getContainerList().remove(this);
	}

	@Override
	public void restart(boolean force) {
		stop(force);
		start(force);
	}

	@Override
	public void start(boolean force) {
		super.getParent().getExecutor().executeCommand("container-start " + (force ? "--force " : "") + super.getName());
		super.getParent().getExecutor().waitForProvisioning(this);
		super.setOnline(true);
	}

	@Override
	public void stop(boolean force) {
		super.getParent().getExecutor().executeCommand("container-stop " + (force ? "--force " : "") + super.getName());
		super.getParent().getExecutor().waitForContainerStop(this);
		super.setOnline(false);
	}

	@Override
	public void kill() {
		super.getNode().getExecutor().executeCommand("pkill -9 -f " + super.getName());
		super.setOnline(false);
	}

	@Override
	public List<String> executeCommands(String... commands) {
		return super.getExecutor().executeCommands(commands);
	}

	@Override
	public List<String> executeNodeCommands(String... commands) {
		return super.getNode().getExecutor().executeCommands(commands);
	}

	@Override
	public void waitForProvisioning() {
		waitForProvisionStatus("success");
	}

	@Override
	public void waitForProvisioning(int time) {
		getExecutor().waitForProvisioning(this, time);
	}

	@Override
	public void waitForProvisionStatus(String status) {
		getExecutor().waitForProvisionStatus(this, status);
	}

	@Override
	public void waitForProvisionStatus(String status, int time) {
		getExecutor().waitForProvisionStatus(this, status, time);
	}

	/**
	 * Delete SSH container folder from static node.
	 */
	private void clean() {
		log.info("Deleting container folder on " + super.getNode().getHost());

		final String path;
		final String workDir = OptionUtils.getString(super.getOptions(), Option.WORKING_DIRECTORY);
		if (!("".equals(workDir)) || !("".equals(SystemProperty.getWorkingDirectory()))) {
			// Decide if working directory was set on ssh and if not set the system property as default
			path = "".equals(workDir) ? SystemProperty.getWorkingDirectory() : workDir;
		} else {
			path = "containers";
		}
		// Executor needs to be connected before executing command
		super.getNode().getExecutor().connect();
		super.getNode().getExecutor().executeCommand("rm -rf " + path + File.separator + super.getName());
	}

	/**
	 * Ssh builder class - this class returns the SshContainer object and it is the only way the ssh container should be built.
	 */
	public static class SshBuilder {
		// Container instance
		private Container container;

		/**
		 * Constructor.
		 *
		 * @param copy container that will be copied
		 */
		public SshBuilder(Container copy) {
			if (copy != null) {
				final Map<Option, List<String>> opts = new HashMap<>();
				for (Map.Entry<Option, List<String>> optionListEntry : copy.getOptions().entrySet()) {
					// We need to copy the lists aswell
					final List<String> listCopy = new ArrayList<>();
					listCopy.addAll(optionListEntry.getValue());
					opts.put(optionListEntry.getKey(), listCopy);
				}
				this.container = new SshContainer()
						.name(copy.getName())
						.user(copy.getUser())
						.password(copy.getPassword())
						.parent(copy.getParent())
						.parentName(copy.getParentName())
						// We need to create a new instance of the node for the cloning case, otherwise all clones
						// would have the same object instance
						.node(Node.builder()
								.host(copy.getNode().getHost())
								.port(copy.getNode().getPort())
								.username(copy.getNode().getUsername())
								.password(copy.getNode().getPassword())
								.build())
						// Same as node
						.options(opts);
			} else {
				this.container = new SshContainer();
				// Set the empty node
				container.setNode(Node.builder().build());
			}
		}

		/**
		 * Setter.
		 *
		 * @param name name
		 * @return this
		 */
		public SshBuilder name(String name) {
			container.setName(name);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param user user
		 * @return user
		 */
		public SshBuilder user(String user) {
			container.getNode().setUsername(user);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param password password
		 * @return this
		 */
		public SshBuilder password(String password) {
			container.getNode().setPassword(password);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param node node
		 * @return this
		 */
		public SshBuilder node(Node node) {
			container.setNode(node);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param host host
		 * @return this
		 */
		public SshBuilder node(String host) {
			container.setNode(Node.builder().host(host).build());

			return this;
		}

		/**
		 * Setter.
		 *
		 * @param host host
		 * @param user user
		 * @param password password
		 * @return this
		 */
		public SshBuilder node(String host, String user, String password) {
			container.setNode(
					Node.builder()
							.host(host)
							.username(user)
							.password(password)
							.build()
			);

			return this;
		}

		/**
		 * Setter.
		 *
		 * @param host host
		 * @param port port
		 * @param user user
		 * @param password password
		 * @return this
		 */
		public SshBuilder node(String host, int port, String user, String password) {
			container.setNode(
					Node.builder()
							.host(host)
							.port(port)
							.username(user)
							.password(password)
							.build()
			);

			return this;
		}

		/**
		 * Setter.
		 *
		 * @param parentName parent name
		 * @return this
		 */
		public SshBuilder parentName(String parentName) {
			container.setParentName(parentName);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param parent parent
		 * @return this
		 */
		public SshBuilder parent(Container parent) {
			container.setParent(parent);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param profiles profiles array
		 * @return this
		 */
		public SshBuilder profiles(String... profiles) {
			OptionUtils.set(container.getOptions(), Option.PROFILE, profiles);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param commands commands array
		 * @return this
		 */
		public SshBuilder commands(String... commands) {
			OptionUtils.set(container.getOptions(), Option.COMMANDS, commands);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param version version
		 * @return this
		 */
		public SshBuilder version(String version) {
			OptionUtils.set(container.getOptions(), Option.VERSION, version);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param envs environment variables
		 * @return this
		 */
		public SshBuilder env(String... envs) {
			OptionUtils.set(container.getOptions(), Option.ENV, envs);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param jvmOpts JVM options
		 * @return this
		 */
		public SshBuilder jvmOpts(String... jvmOpts) {
			OptionUtils.set(container.getOptions(), Option.JVM_OPTS, jvmOpts);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param workingDirectory file path to working directory for SSH container
		 * @return this
		 */
		public SshBuilder directory(String workingDirectory) {
			OptionUtils.set(container.getOptions(), Option.WORKING_DIRECTORY, workingDirectory);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param resolver one of resolver enum
		 * @return this
		 */
		public SshBuilder resolver(Resolver resolver) {
			OptionUtils.set(container.getOptions(), Option.RESOLVER, resolver.toString());
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param zkPass zookeeper password
		 * @return this
		 */
		public SshBuilder zookeeperPassword(String zkPass) {
			OptionUtils.set(container.getOptions(), Option.ZOOKEEPER_PASSWORD, zkPass);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param manualIp manual ip
		 * @return this
		 */
		public SshBuilder manualIp(String manualIp) {
			OptionUtils.set(container.getOptions(), Option.MANUAL_IP, manualIp);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param addr bind address
		 * @return this
		 */
		public SshBuilder bindAddress(String addr) {
			OptionUtils.set(container.getOptions(), Option.BIND_ADDRESS, addr);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param datastore datastore options
		 * @return this
		 */
		public SshBuilder datastore(String... datastore) {
			OptionUtils.set(container.getOptions(), Option.DATASTORE_OPTION, datastore);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param passPhrase pass phrase
		 * @return this
		 */
		public SshBuilder passPhrase(String passPhrase) {
			OptionUtils.set(container.getOptions(), Option.PASS_PHRASE, passPhrase);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param port ssh port
		 * @return this
		 */
		public SshBuilder port(int port) {
			OptionUtils.set(container.getOptions(), Option.PORT, String.valueOf(port));
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param privateKey private key path
		 * @return this
		 */
		public SshBuilder privateKey(String privateKey) {
			OptionUtils.set(container.getOptions(), Option.PRIVATE_KEY, privateKey);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param minPort min port
		 * @return this
		 */
		public SshBuilder minPort(int minPort) {
			OptionUtils.set(container.getOptions(), Option.MIN_PORT, String.valueOf(minPort));
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param repos fallback repos
		 * @return this
		 */
		public SshBuilder fallbackRepos(String... repos) {
			OptionUtils.set(container.getOptions(), Option.FALLBACK_REPOS, repos);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param uri proxy uri
		 * @return this
		 */
		public SshBuilder proxyUri(String uri) {
			OptionUtils.set(container.getOptions(), Option.PROXY_URI, uri);
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param port max port
		 * @return this
		 */
		public SshBuilder maxPort(int port) {
			OptionUtils.set(container.getOptions(), Option.MAX_PORT, String.valueOf(port));
			return this;
		}

		/**
		 * Setter.
		 *
		 * @param retries ssh retries
		 * @return this
		 */
		public SshBuilder sshRetries(int retries) {
			OptionUtils.set(container.getOptions(), Option.SSH_RETRIES, String.valueOf(retries));
			return this;
		}

		/**
		 * Sets the "--with-admin-access" flag.
		 *
		 * @return this
		 */
		public SshBuilder withAdminAccess() {
			OptionUtils.set(container.getOptions(), Option.WITH_ADMIN_ACCESS, "");
			return this;
		}

		/**
		 * Sets the "--disable-distribution-upload" flag.
		 *
		 * @return this
		 */
		public SshBuilder disableDistributionUpload() {
			OptionUtils.set(container.getOptions(), Option.DISABLE_DISTRIBUTION_UPLOAD, "");
			return this;
		}

		/**
		 * Setter for additional create options that does not have special method.
		 *
		 * @param options options string
		 * @return this
		 * @deprecated Use other setters, they should be complete.
		 */
		@Deprecated
		public SshBuilder options(String options) {
			final List<String> old = OptionUtils.get(container.getOptions(), Option.OTHER);
			old.add(options);
			container.getOptions().put(Option.OTHER, old);
			return this;
		}

		/**
		 * Builds the instance.
		 *
		 * @return sshcontainer instance
		 */
		public Container build() {
			return container;
		}
	}
}
