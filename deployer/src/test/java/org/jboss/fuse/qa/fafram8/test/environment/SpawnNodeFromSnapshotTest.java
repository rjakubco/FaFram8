package org.jboss.fuse.qa.fafram8.test.environment;

import org.jboss.fuse.qa.fafram8.environment.OpenStackProvisionManager;

import org.junit.AfterClass;
import org.junit.Test;

import org.openstack4j.model.compute.Server;

import static org.junit.Assert.*;

/**
 * Created by ecervena on 24.9.15.
 */
public class SpawnNodeFromSnapshotTest {

    @Test
    public void spawnNewNodeTest() {
        OpenStackProvisionManager osm = new OpenStackProvisionManager();
        osm.spawnNewNode("hello-kitty");
        assertEquals("Wrong name returned.", "fafram8-hello-kitty", osm.getServerByName("fafram8-hello-kitty").getName());
    }

    @AfterClass
    public void clean() {
        OpenStackProvisionManager osm = new OpenStackProvisionManager();
        Server server = osm.getServerByName("fafram8-hello-kitty");
        osm.getOs().compute().servers().delete(server.getId());
        System.out.println("Instance " + server.getName() + " with ID " + server.getId() + " has been deleted.");
    }
}
