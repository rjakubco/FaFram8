package org.jboss.fuse.qa.fafram8.ConfigParser;

import org.jboss.fuse.qa.fafram8.manager.Container;
import org.jboss.fuse.qa.fafram8.resource.Fafram;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

/**
 * TODO(ecervena): this is only test implementation. should be singleton
 * <p/>
 * Created by ecervena on 9/8/15.
 */
public final class ConfigurationParser {

	public static ConfigurationParser configurationParser = null;

	@Getter
	private static List<Container> containerList = new LinkedList<Container>();

	/**
	 * Constructor.
	 */
	private ConfigurationParser() {
	}

	/**
	 * TODO(ecervena): parseConfigurationFile should be call once in some starting point
	 * Parses the configuration file.
	 *
	 * @param path path
	 */
	public static void parseConfigurationFile(String path) {
		//TODO(ecervena): implement
		final Container container1 = new Container("root");
		container1.setRoot(true);
		final Container container2 = new Container("node1");
		containerList.add(container1);
		containerList.add(container2);
	}

	public static void populateFaframContainerList() {
		for(Container container: containerList) {
			Fafram.addContainer(container);
		}
	}

	/**
	 * Should be used to access ConfigurationParser singleton instance.
	 *
	 * @return ConfigurationParser instance
	 */
	public static ConfigurationParser getInstance() {
		if (configurationParser == null) {
			configurationParser = new ConfigurationParser();
		}
		return configurationParser;
	}
}
