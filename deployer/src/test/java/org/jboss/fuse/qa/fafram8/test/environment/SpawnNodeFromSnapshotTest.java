package org.jboss.fuse.qa.fafram8.test.environment;

import com.sun.org.apache.xpath.internal.SourceTree;
import org.jboss.fuse.qa.fafram8.environment.OpenStackClient;
import org.jboss.fuse.qa.fafram8.environment.OpenStackManager;
import org.junit.After;
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

    //@Test
    public void spawnNewNodeTest() {
        OpenStackManager osm = new OpenStackManager();
        osm.spawnNewNode("hello-kitty");
        Map<String, String> filter = new HashMap<String,String>();
        filter.put("name", "fafram8-hello-kitty");
        List<Server> serverList = (List<Server>) osm.getOs().compute().servers().list(filter);
        System.out.println("Instance " + serverList.get(0).getName() + " with ID " + serverList.get(0).getId() + " created.");
        assertEquals("Should return 1 server", 1, serverList.size());
    }

    //@Test
    public void printPools() {
        OpenStackManager osm = new OpenStackManager();
        osm.printAvailableIpPlools();
    }

    //@After
    public void clean() {
        OpenStackManager osm = new OpenStackManager();
        //Map<String, String> filter = new HashMap<String,String>();
        //filter.put("name", "fafram8-hello-kitty");
        Server server = osm.getServerByName("fafram8-hello-kitty");
        //List<Server> serverList = (List<Server>) osm.getOs().compute().servers().list(filter);
        osm.getOs().compute().servers().delete(server.getId());
        System.out.println("Instance " + server.getName() + " with ID " + server.getId() + " has been deleted.");
    }

    @Test
    public void accessTest() {
        OSClient os = OSFactory.clientFromAccess(OpenStackClient.getInstance().getAccess());
        System.out.println(os.compute().servers().get("1111"));
    }
}
