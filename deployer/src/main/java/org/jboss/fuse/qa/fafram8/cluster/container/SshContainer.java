package org.jboss.fuse.qa.fafram8.cluster.container;

import org.jboss.fuse.qa.fafram8.cluster.Node;
import org.jboss.fuse.qa.fafram8.exception.FaframException;
import org.jboss.fuse.qa.fafram8.manager.ContainerManager;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

/**
 * Class representing ssh container. Instances of this class should and can be created just with SshBuilder class.
 * Created by avano on 1.2.16.
 */
@Slf4j
public class SshContainer extends Container {
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
		return new SshBuilder(new SshContainer());
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
		if (super.getParent() == null) {
			// Search the parent by its name
			final Container parent = ContainerManager.getContainer(super.getParentName());
			if (parent == null) {
				throw new FaframException(String.format("Specified parent (%s) of container %s does not exist in container list!",
						super.getParentName(), super.getName()));
			}
			super.setParent(parent);
		}
		String profilesString = "";

		for (String profile : super.getProfiles()) {
			profilesString += " --profile " + profile;
		}

		super.getParent().getExecutor().executeCommand(String.format("container-create-ssh --user %s --password %s --host %s%s %s",
				super.getNode().getUsername(), super.getNode().getPassword(), super.getNode().getHost(), profilesString, super.getName()));
		super.getParent().getExecutor().waitForProvisioning(this);
		super.setOnline(true);
	}

	@Override
	public void destroy() {
		log.info("Destroying container " + super.getName());
		super.getParent().getExecutor().executeCommand("container-delete " + super.getName());
	}

	@Override
	public void restart() {
		stop();
		start();
	}

	@Override
	public void start() {
		super.getParent().getExecutor().executeCommand("container-start " + super.getName());
		super.getParent().getExecutor().waitForProvisioning(this);
		super.setOnline(true);
	}

	@Override
	public void stop() {
		super.getParent().getExecutor().executeCommand("container-stop " + super.getName());
		super.getParent().getExecutor().waitForProvisionStatus(this, "stopped");
		super.setOnline(false);
	}

	@Override
	public void kill() {
	}

	@Override
	public String executeCommand(String command) {
		return super.getParent().getExecutor().executeCommand(command);
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
				this.container = copy;
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
			container.setProfiles(Arrays.asList(profiles));
			return this;
		}

		/**
		 * Builds the instance.
		 *
		 * @return sshcontainer instance
		 */
		public Container build() {
			return new SshContainer()
					.name(container.getName())
					.user(container.getUser())
					.password(container.getPassword())
					.parent(container.getParent())
					.parentName(container.getParentName())
					.node(container.getNode())
					.executor(null)
					.profiles(container.getProfiles());
		}
	}
}
