package org.jboss.fuse.qa.fafram8.property;

/**
 * Fafram system properties names.
 * Created by avano on 20.8.15.
 */
public final class FaframConstant {

	/**
	 * Private constructor.
	 */
	private FaframConstant() {
	}

	// Host IP address
	public static final String HOST = "host";

	// Host port
	public static final String HOST_PORT = "host.port";

	// Host login
	public static final String HOST_USER = "host.user";

	// Host password
	public static final String HOST_PASSWORD = "host.password";

	// Fuse user
	public static final String FUSE_USER = "fuse.user";

	// Fuse password
	public static final String FUSE_PASSWORD = "fuse.password";

	// Product group id
	public static final String FUSE_GROUP = "fuse.group";

	// Product artifact id
	public static final String FUSE_ID = "fuse.id";

	// Product version
	public static final String FUSE_VERSION = "fuse.version";

	// Product URL
	public static final String FUSE_ZIP = "fuse.zip";

	// Full path to unzipped product directory
	public static final String FUSE_PATH = "fuse.path";

	// Karaf start wait time
	public static final String START_WAIT_TIME = "start.wait.time";

	// Karaf stop wait time
	public static final String STOP_WAIT_TIME = "stop.wait.time";

	// Fabric provision wait time
	public static final String PROVISION_WAIT_TIME = "provision.wait.time";

	// Patch install wait time
	public static final String PATCH_WAIT_TIME = "patch.wait.time";

	// Keep folder
	public static final String KEEP_FOLDER = "keepFolder";

	// Name of the working folder on remote host
	public static final String FAFRAM_FOLDER = "fafram.folder";

	// Patch
	public static final String PATCH = "patch";

	// Fabric
	public static final String FABRIC = "fabric";

	// Special working directory on machine where fafram folder should be created.
	// Use when working on perf machines.
	public static final String WORKING_DIRECTORY = "fafram.working.directory";

	// Patches folder
	public static final String PATCH_DIR = "fafram.patch.dir";

	// If "true" OpenStackProvisionManager will not release OS nodes after test
	public static final String KEEP_OS_RESOURCES = "keep.os.resources";

	// If "true" fafram will use local deployer
	public static final String LOCAL_DEPLOYMENT = "local.deployment";

	// Patches folder
	public static final String PATCH_DIR = "fafram.patch.dir";

	// Skip default user flag
	public static final String SKIP_DEFAULT_USER = "fafram.skip.default.user";

	//Specify infrastructure provider class name a.k.a. provisioning provider
	public static final String PROVIDER = "provider.class.name";
}
