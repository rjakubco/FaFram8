package org.jboss.fuse.qa.fafram8.environment;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jboss.fuse.qa.fafram8.ConfigParser.ConfigurationParser;
import org.jboss.fuse.qa.fafram8.exception.UniqueNodeNameException;
import org.jboss.fuse.qa.fafram8.manager.Container;
import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.*;
import org.openstack4j.model.compute.ext.DNSEntry;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.builder.NetworkBuilder;
import org.openstack4j.openstack.OSFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * OpenStackManager class used for calling all OpenStack node operations. Using authenticated OpenStackClient singleton.
 *
 * Created by ecervena on 24.9.15.
 * TODO(ecervena): this should be probably singleton
 */
@Slf4j
public class OpenStackManager {

    //List of floating addresses allocated by OpenStackManager
    private static List<FloatingIP> floatingIPs = new LinkedList<>();

    //List of all created OpenStack nodes a.k.a. servers
    private static List<Server> servers = new LinkedList<>();

    //Authenticated OpenStackClient instance
    @Getter
    private OSClient os = OpenStackClient.getInstance();

    /**
     * Create new OpenStack node. Method will create Server object model, boot it and wait for active status.
     *
     * @param nodeName name of the new node
     */
    public void spawnNewNode(String nodeName) {
        ServerCreate server = os
                .compute()
                .servers()
                .serverBuilder()
                .image("a61880d9-3cc3-40df-b172-d3282104adb4")
                .name("fafram8-" + nodeName)
                .flavor("3")
                .keypairName("ecervena")
                .build();
        servers.add(os.compute().servers().bootAndWaitActive(server, 120000));
    }


    /**
     * Method for deleting OpenStack node by server name. All nodes created  by OpenStackManager have "fafram8-" prefix.
     *
     * @param nodeName name of the node
     */
    public void deleteNode(String nodeName) {
        os.compute().servers().delete(getServerByName(nodeName).getId());
    }


    /**
     * Method for getting Server a.k.a OpenStack node object model. All nodes created  by OpenStackManager
     * have "fafram8-" prefix.
     *
     * @param nodeName name of the node
     * @return Server
     */
    public Server getServerByName(String nodeName) {
        Map<String, String> filter = new HashMap<String,String>();
        filter.put("name", nodeName);
        List<Server> serverList = (List<Server>) os
                .compute()
                .servers()
                .list(filter);
        if (serverList.size() != 1)
            {throw new UniqueNodeNameException("Node name is not unique. More then 1 (" + serverList.size() + ") node with specified name: " + nodeName + " detected");}
        else {return serverList.get(0);}
    }

    /**
     * This method will use ConfigurationParser singleton to parse XML representation of OpenStack infrastructure
     * and spawn thread for each container to create its OpenStack node. Each thread will create one OpenStack node,
     * assign its serverId to container object model and register new server to OpenStackManager's ServerList.
     * IP addresses are assigned to containers later. Root container will get floating public IP and register it.
     * Others will get only local IP.
     *
     * TODO(ecervena): configuration parser uses only fake config path. Change it when parser will be fully implemented
     */
    public void spawnInfrastructure() {
        log.info("Spawning OpenStack infrastructure.");
        ConfigurationParser cp = ConfigurationParser.getInstance();
        cp.parseConfigurationFile("some/fake/file/path");
        List<Container> containerList = cp.getContainerList();
        ServerInvokerPool.spawnServers(containerList, os.getAccess());
        for(Container container: containerList) {
            Server server = getServerByName("fafram8-" + container.getName());
            container.setOpenStackServerId(server.getId());

            if(container.isRoot()) {
                String ip = assignFloatingAddress(server.getId());
                log.debug("Assiging public IP: " + ip + " for container: " + container.getName());
                container.setHostIP(ip);
                System.setProperty(FaframConstant.HOST, ip);
            } else {
                //fuseqe-lab has only 1 address type "fuseqe-lab-1" with only one address called NovaAddress
                container.setHostIP(server.getAddresses().getAddresses("fuseqe-lab-1").get(0).getAddr());
                log.debug("Assigning local IP: " + server.getAddresses().getAddresses("fuseqe-lab-1").get(0).getAddr() + " for container: " + container.getName());
            }
        }
    }

    /**
     * Release allocated OpenStack resources. Method will delete created servers and release allocated floating IPs.
     */
    public void releaseResources() {
        if (SystemProperty.isKeepOsResources().equals("true")) {
            log.info("Keeping OpenStack resources. Don't forget to release them later!");
            return;
        }
        log.info("Releasing allocated OpenStack resources.");
        for(FloatingIP ip: floatingIPs) {
            log.info("Deallocating floating IP: " + ip.getFloatingIpAddress());
            os.compute().floatingIps().deallocateIP(ip.getId());
        }
        for(Server server: servers) {
            log.info("Terminating node: " + server.getName());
            os.compute().servers().delete(server.getId());
        }
        log.info("All OpenStack resources has been released successfully");
    }

    //TODO(ecervena): remove this later
    //Help method only for development use
    public void printAvailableIpPlools() {
        for(String poolName: os.compute().floatingIps().getPoolNames()) {
            System.out.println("Pool: " + poolName);
        }
    }

    /**
     * Assign floating IP address to specified server.
     * @param serverID ID of the server
     * @return floating IP assigned to server
     */
    public String assignFloatingAddress(String serverID) {
        FloatingIP ip = os.compute().floatingIps().allocateIP("public");
        floatingIPs.add(ip);
        Server server = os.compute().servers().get(serverID);
        os.compute().floatingIps().addFloatingIP(server,ip.getFloatingIpAddress());
        return ip.getFloatingIpAddress();
    }

    //TODO(ecervena): remove this later
    //Help method only for development use
    public void printAddresses(Map<String, List<? extends Address>> addresses) {
        for(String addressType: addresses.keySet()) {
            System.out.println("Address type: " + addressType);
            for (Address address: addresses.get(addressType)) {
                System.out.println("---" + address.getAddr());
            }
        }
    }

    /**
     * Register server to OpenStackManager's "register"
     * @param server
     */
    public static void registerServer(Server server) {
        servers.add(server);
    }


}
