package org.jboss.fuse.qa.fafram8.downloader;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.apache.maven.shared.invoker.PrintStreamHandler;

import org.jboss.fuse.qa.fafram8.executor.Executor;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;

/**
 * Downloader class.
 * Created by avano on 19.8.15.
 */
@Slf4j
public final class Downloader {
	// File separator
	private static final String SEP = File.separator;

	/**
	 * Private constructor.
	 */
	private Downloader() {
	}

	/**
	 * Downloads/Gets the product zip on localhost.
	 *
	 * @return path to the downloaded zip
	 */
	public static String getProduct() {
		// If the FUSE_ZIP is not set, get the artifact from maven
		if (SystemProperty.FUSE_ZIP == null) {
			log.info("Getting product from local repository");
			return getProductFromMaven();
		} else {
			// We are using custom zip on local
			log.info("Getting product from " + SystemProperty.FUSE_ZIP);
			return getProductFromUrl();
		}
	}

	/**
	 * Downloads/Gets the product zip to the remote host.
	 *
	 * @param executor executor with assign ssh client
	 * @return absolute path to the file
	 */
	public static String getProduct(Executor executor) {
		// We are using custom zip on local
		log.info("Getting product from " + SystemProperty.FUSE_ZIP);
		return getProductFromUrl(executor);
	}

	/**
	 * Gets the product zip from maven.
	 *
	 * @return absolute path to the file
	 */
	private static String getProductFromMaven() {
		locateMaven();
		final String localRepo = getMavenLocalRepository();
		return getArtifactPath(localRepo);
	}

	/**
	 * Gets the product zip from url on localhost.
	 *
	 * @return absolute path to the file
	 */
	private static String getProductFromUrl() {
		// Get the protocol from the property
		final String protocol = StringUtils.substringBefore(SystemProperty.FUSE_ZIP, ":");
		String location;
		switch (protocol) {
			case "http":
				// wget
				throw new UnsupportedOperationException("not implemented");
			case "scp":
				throw new UnsupportedOperationException("not implemented");
			case "file":
				// Strip the protocol from the path
				location = StringUtils.substringAfter(SystemProperty.FUSE_ZIP, ":");
				break;
			default:
				throw new RuntimeException("Unsupported protocol " + protocol);
		}
		return location;
	}

	/**
	 * TODO(rjakubco): working dir?
	 * TODO(avano): other possible protocols
	 * Gets the product zip from url on remote.
	 *
	 * @return absolute path to the file
	 */
	private static String getProductFromUrl(Executor executor) {
		// Get the protocol from the property
		final String protocol = StringUtils.substringBefore(SystemProperty.FUSE_ZIP, ":");
		String location;
		switch (protocol) {
			case "http":
				log.info(executor.executeCommand(
						"wget --no-check-certificate -q -P " + SystemProperty.FAFRAM_FOLDER + " "
								+ SystemProperty.FUSE_ZIP));
				location = executor.executeCommand("ls -d -1 $PWD" + SEP + SystemProperty.FAFRAM_FOLDER + SEP + "*");
				break;
			case "scp":
				throw new UnsupportedOperationException("not implemented");
			case "file":
				// Strip the protocol from the path
				location = StringUtils.substringAfter(SystemProperty.FUSE_ZIP, ":");
				break;
			default:
				throw new RuntimeException("Unsupported protocol " + protocol);
		}

		return location;
	}

	/**
	 * Checks for the maven and sets the maven.home property used by maven-invoker.
	 */
	private static void locateMaven() {
		log.debug("maven.home property is " + System.getProperty("maven.home"));
		if (System.getProperty("maven.home") != null) {
			return;
		}

		log.debug("M2_HOME system property is " + System.getProperty("M2_HOME"));
		if (System.getProperty("M2_HOME") != null) {
			System.setProperty("maven.home", System.getProperty("M2_HOME"));
			return;
		}

		log.debug("M2_HOME env property is " + System.getenv("M2_HOME"));
		if (System.getenv("M2_HOME") != null) {
			System.setProperty("maven.home", System.getenv("M2_HOME"));
			return;
		}

		final String path = System.getenv("PATH");
		final String[] pathParts = path.split(File.pathSeparator);

		for (String part : pathParts) {
			log.debug("Checking path for mvn: " + part);
			if (part.contains("mvn") || part.contains("maven")) {
				// Strip the /bin from mvn path if found
				String mvnLocation;
				if (part.contains("bin")) {
					mvnLocation = part.substring(0, part.indexOf("bin"));
				} else {
					mvnLocation = part;
				}

				System.setProperty("maven.home", mvnLocation);
				log.debug("Maven found in " + mvnLocation);

				return;
			}
		}
	}

	/**
	 * Gets the maven local repository path.
	 *
	 * @return local repository path
	 */
	private static String getMavenLocalRepository() {
		// Get effective settings from maven
		final InvocationRequest req = new DefaultInvocationRequest();
		req.setGoals(Collections.singletonList("help:effective-settings"));

		final Invoker invoker = new DefaultInvoker();

		// Redirect invoker output to variable
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PrintStream ps = new PrintStream(baos);
		invoker.setOutputHandler(new PrintStreamHandler(ps, true));

		try {
			invoker.execute(req);
		} catch (MavenInvocationException e) {
			e.printStackTrace();
		}

		// Close streams
		ps.close();
		String output = baos.toString();
		try {
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Parse local repository
		output = output.substring(output.indexOf("localRepository"));
		output = output.substring(output.indexOf(">") + 1, output.indexOf("<"));

		log.debug("Local repository path is " + output);
		return output;
	}

	/**
	 * Gets the artifact path.
	 *
	 * @return artifact path
	 */
	private static String getArtifactPath(String localRepositoryPath) {
		final String sep = File.separator;
		final String groupPath = SystemProperty.FUSE_GROUP.replaceAll("\\.", sep + sep);

		// Construct the path to the artifact in local repo
		final String artifactPath = localRepositoryPath + sep + groupPath + sep + SystemProperty.FUSE_ID + sep
				+ SystemProperty.FUSE_VERSION + sep + SystemProperty.FUSE_ID + "-" + SystemProperty.FUSE_VERSION + ""
				+ ".zip";

		log.debug("Artifact path is " + artifactPath);

		final File artifact = new File(artifactPath);
		return artifact.getAbsolutePath();
	}
}
