package org.jboss.fuse.qa.fafram8.ConfigParser;

import lombok.Getter;
import org.jboss.fuse.qa.fafram8.manager.Container;

import java.util.LinkedList;
import java.util.List;

/**
 * TODO this is only test implementation
 *
 * Created by ecervena on 9/8/15.
 */
public class ConfigurationParser {

    @Getter
    private List<Container> containerList = new LinkedList<Container>();

    public ConfigurationParser() {}

    public void parseConfigurationFile(String path) {
        //TODO
        Container container = new Container("node3","172.16.116.22");
        containerList.add(container);
    }

}
