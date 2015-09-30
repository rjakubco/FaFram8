package org.jboss.fuse.qa.fafram8.test.environment;

import com.sun.org.apache.xpath.internal.SourceTree;
import org.jboss.fuse.qa.fafram8.environment.OpenStackClient;
import org.jboss.fuse.qa.fafram8.environment.OpenStackManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ext.DomainEntry;
import org.openstack4j.model.network.Network;
import org.openstack4j.openstack.OSFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by ecervena on 24.9.15.
 */
public class SpawnNodeFromSnapshotTest {

    @Test
    public void spawnNewNodeTest() {
        OpenStackManager osm = new OpenStackManager();
        osm.spawnNewNode("hello-kitty");
        assertEquals("Wrong name returned.", "fafram8-hello-kitty", osm.getServerByName("fafram8-hello-kitty").getName());
    }

    @AfterClass
    public void clean() {
        OpenStackManager osm = new OpenStackManager();
        Server server = osm.getServerByName("fafram8-hello-kitty");
        osm.getOs().compute().servers().delete(server.getId());
        System.out.println("Instance " + server.getName() + " with ID " + server.getId() + " has been deleted.");
    }
}
