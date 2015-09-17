package org.jboss.fuse.qa.fafram8.property;

/**
 * System property class.
 * Created by avano on 20.8.15.
 */
public final class SystemProperty {

	/**
	 * Private constructor.
	 */
	private SystemProperty() {
	}

	/**
	 * Getter.
	 * @return remote host
	 */
	public static String getHost() {
		return System.getProperty(FaframConstant.HOST);
	}

	/**
	 * Getter.
	 * @return host ssh port
	 */
	public static int getHostPort() {
		return Integer.parseInt(System.getProperty(FaframConstant.HOST_PORT, "22"));
	}

	/**
	 * Getter.
	 * @return host login
	 */
	public static String getHostUser() {
		return System.getProperty(FaframConstant.HOST_USER, "fuse");
	}

	/**
	 * Getter.
	 * @return host password
	 */
	public static String getHostPassword() {
		return System.getProperty(FaframConstant.HOST_PASSWORD, "fuse");
	}

	/**
	 * Getter.
	 * @return fuse user
	 */
	public static String getFuseUser() {
		return System.getProperty(FaframConstant.FUSE_USER, "admin");
	}

	/**
	 * Getter.
	 * @return fuse password
	 */
	public static String getFusePassword() {
		return System.getProperty(FaframConstant.FUSE_PASSWORD, "admin");
	}

	/**
	 * Getter.
	 * @return product group id
	 */
	public static String getFuseGroup() {
		return System.getProperty(FaframConstant.FUSE_GROUP);
	}

	/**
	 * Getter.
	 * @return product artifact id
	 */
	public static String getFuseId() {
		return System.getProperty(FaframConstant.FUSE_ID);
	}

	/**
	 * Getter.
	 * @return product version
	 */
	public static String getFuseVersion() {
		return System.getProperty(FaframConstant.FUSE_VERSION);
	}

	/**
	 * Getter.
	 * @return product url
	 */
	public static String getFuseZip() {
		return System.getProperty(FaframConstant.FUSE_ZIP);
	}

	/**
	 * Getter.
	 * @return karaf start wait time
	 */
	public static int getStartWaitTime() {
		return Integer.parseInt(System.getProperty(FaframConstant.START_WAIT_TIME, "120"));
	}

	/**
	 * Getter.
	 * @return karaf stop wait time
	 */
	public static int getStopWaitTime() {
		return Integer.parseInt(System.getProperty(FaframConstant.STOP_WAIT_TIME, "30"));
	}

	/**
	 * Getter.
	 * @return fabric provision wait time
	 */
	public static int getProvisionWaitTime() {
		return Integer.parseInt(System.getProperty(FaframConstant.PROVISION_WAIT_TIME, "300"));
	}

	/**
	 * Getter.
	 * @return patch install wait time
	 */
	public static int getPatchWaitTime() {
		return Integer.parseInt(System.getProperty(FaframConstant.PATCH_WAIT_TIME, "120"));
	}

	/**
	 * Getter.
	 * @return keep folder
	 */
	public static boolean isKeepFolder() {
		return System.getProperty(FaframConstant.KEEP_FOLDER) != null;
	}

	/**
	 * Getter.
	 * @return fafram folder name
	 */
	public static String getFaframFolder() {
		return System.getProperty(FaframConstant.FAFRAM_FOLDER, "fafram");
	}

	/**
	 * Getter.
	 * @return fabric flag
	 */
	public static boolean isFabric() {
		return System.getProperty(FaframConstant.FABRIC) != null;
	}

	/**
	 * Getter.
	 * @return fabric settings
	 */
	public static String getFabric() {
		return System.getProperty(FaframConstant.FABRIC);
	}

	/**
	 * Getter.
	 * @return fuse path
	 */
	public static String getFusePath() {
		return System.getProperty(FaframConstant.FUSE_PATH);
	}

	/**
	 * Getter.
	 * @return patch
	 */
	public static String getPatch() {
		return System.getProperty(FaframConstant.PATCH);
	}

	/**
	 * Getter.
	 * @return absolute path to working directory
	 */
	public static String getWorkingDirectory() {
		return System.getProperty(FaframConstant.WORKING_DIRECTORY, "");
	}
}
