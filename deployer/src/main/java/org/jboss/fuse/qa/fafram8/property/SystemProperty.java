package org.jboss.fuse.qa.fafram8.property;

/**
 * System property class.
 * Created by avano on 20.8.15.
 */
public class SystemProperty {
	// Host
	public static final String HOST = System.getProperty(FaframConstant.HOST);

	public static final int HOST_PORT = Integer.parseInt(System.getProperty(FaframConstant.HOST_PORT,
			"22"));

	// Host login
	public static final String HOST_USER = System.getProperty(FaframConstant.HOST_USER, "fuse");

	// Host password
	public static final String HOST_PASSWORD = System.getProperty(FaframConstant.HOST_PASSWORD, "fuse");

	// Fuse user
	public static final String FUSE_USER = System.getProperty(FaframConstant.FUSE_USER, "admin");

	// Fuse password
	public static final String FUSE_PASSWORD = System.getProperty(FaframConstant.FUSE_PASSWORD, "admin");

	// Product group id
	public static final String FUSE_GROUP = System.getProperty(FaframConstant.FUSE_GROUP);

	// Product artifact id
	public static final String FUSE_ID = System.getProperty(FaframConstant.FUSE_ID);

	// Product version
	public static final String FUSE_VERSION = System.getProperty(FaframConstant.FUSE_VERSION);

	// Product URL
	public static final String FUSE_ZIP = System.getProperty(FaframConstant.FUSE_ZIP);

	// Karaf start wait time
	public static final int START_WAIT_TIME = Integer.parseInt(System.getProperty(FaframConstant.START_WAIT_TIME,
			"120"));

	// Karaf start wait time
	public static final int STOP_WAIT_TIME = Integer.parseInt(System.getProperty(FaframConstant.STOP_WAIT_TIME,
			"30"));

	// Fabric provision wait time
	public static final int PROVISION_WAIT_TIME = Integer.parseInt(System.getProperty(
			FaframConstant.PROVISION_WAIT_TIME, "300"));

	// Patch install wait time
	public static final int PATCH_WAIT_TIME = Integer.parseInt(System.getProperty(FaframConstant.PATCH_WAIT_TIME,
			"120"));

	// Keep folder
	public static final String KEEP_FOLDER = System.getProperty(FaframConstant.KEEP_FOLDER);

	// Name of the working dir on remote host
	public static final String FAFRAM_FOLDER = System.getProperty(FaframConstant.FAFRAM_FOLDER, "fafram");
}
