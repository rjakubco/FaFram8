package org.jboss.fuse.qa.fafram8.configuration;

import org.jboss.fuse.qa.fafram8.manager.Container;
import org.jboss.fuse.qa.fafram8.property.FaframConstant;
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

	private static ConfigurationParser configurationParser = null;

	@Getter
	private static List<Container> containerList = new LinkedList<>();

	/**
	 * Constructor.
	 */
	private ConfigurationParser() {
	}

	/**
	 * TODO(ecervena): parseConfigurationFile should be call once in some starting point. Adding containers can be decomposed
	 * <p/>
	 * Parses the configuration file.
	 *
	 * @param path path to xml configuration file
	 */
	public static void parseConfigurationFile(String path) {
		//TODO(ecervena): implement
		final Container container1 = new Container("root");
		container1.setRoot(true);
		final Container container2 = new Container("node1");
		Fafram.addContainer(container1);
		Fafram.addContainer(container2);
	}

	/**
	 * Depending on configuration file sets HOST to trigger remote deployment.
	 */
	public static void setDeployer() {
		//If local deployment do nothing

		//If remote deployment
		System.setProperty(FaframConstant.HOST, "");
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
