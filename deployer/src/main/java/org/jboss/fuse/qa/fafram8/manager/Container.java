package org.jboss.fuse.qa.fafram8.manager;

import lombok.Getter;
import lombok.Setter;

/**
 * Class representing FUSE container.
 * Created by ecervena on 9/8/15.
 */
public class Container {

    public Container(String name, String hostIP) {
        this.name = name;
        this.hostIP = hostIP;
    }

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String hostIP;

    @Getter
    @Setter
    private boolean root = false;

}
