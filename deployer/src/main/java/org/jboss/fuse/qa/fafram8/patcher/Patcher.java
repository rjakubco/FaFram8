package org.jboss.fuse.qa.fafram8.patcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;

import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;
import org.jboss.fuse.qa.fafram8.ssh.SSHClient;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * Patcher class.
 * Created by avano on 1.9.15.
 */
@Slf4j
public class Patcher {
	// TODO(avano): change this
	private static final String DEFAULT_PATCH_LOCATION = "/home/fuse/patches";
	// Do not use patch sys property - the tests are setting the system property for every testMethod
	// and SystemProperty is evaluated when the SystemProperty class is instantiated, therefore it just
	// uses one value for all tests
	
	public static String[] getPatches() {
		return getPatches(null);
	}

	public static String[] getPatches(SSHClient client) {
		String patch = System.getProperty(FaframConstant.PATCH);
		
		// If the property is not set, return empty array
		if (patch == null) {
			return new String[0];
		}

		if ("".equals(patch)) {
			return getPatchFromDefaultLocation();
		}

		String prefix = "";
		try {
			prefix = StringUtils.substringBefore(patch, ":");
		} catch (Exception ignored) {
			// We have no protocol, we are getting patches by name
		}

		switch (prefix) {
			case "http":
				return new String[] {patch};
			case "scp":
				return getPatchFromScp(client);
			case "file":
				return new String[] {patch};
			default:
				return getPatchByName();
		}
	}

	private static String[] getPatchFromDefaultLocation() {
		// Parse the fuse version, returns 6.1 or 6.2, etc.
		final String version = StringUtils.substring(SystemProperty.FUSE_VERSION, 0, 3);

		// Path to default patch directory
		File f = new File(DEFAULT_PATCH_LOCATION + File.separator + "latest");

		// Get only the patches for current version
		FilenameFilter versionFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.contains(version);
			}
		};

		File[] patches = f.listFiles(versionFilter);
		List<String> ret = new ArrayList<>();

		for (File file : patches) {
			ret.add(file.toURI().toString());
		}

		return ret.toArray(new String[ret.size()]);
	}

	private static String[] getPatchFromScp(SSHClient client) {
		throw new UnsupportedOperationException("not implemented");
	}

	private static String[] getPatchByName() {
		String patch = System.getProperty(FaframConstant.PATCH);

		// Parse the fuse version, returns 6.1 or 6.2, etc.
		final String version = StringUtils.substring(SystemProperty.FUSE_VERSION, 0, 3);

		String[] patchNames = patch.split(",");

		Arrays.sort(patchNames);

		// Get all the files
		Collection<File> files = FileUtils.listFiles(new File(DEFAULT_PATCH_LOCATION),
				new WildcardFileFilter("*" + version + "*"), DirectoryFileFilter.DIRECTORY);

		List<File> fileList = new ArrayList<>();
		for (File f : files) {
			if (f.getName().contains(version)) {
				fileList.add(f);
			}
		}

		List<String> patchPathList = new ArrayList<>();

		for (String patchString : patchNames) {
			for (File f : fileList) {
				if (f.getName().contains(patchString)) {
					patchPathList.add(f.toURI().toString());
					break;
				}
			}
		}

		log.debug("Patch location:");
		for (String s : patchPathList) {
			log.debug(s);
		}

		return patchPathList.toArray(new String[patchPathList.size()]);
	}
}