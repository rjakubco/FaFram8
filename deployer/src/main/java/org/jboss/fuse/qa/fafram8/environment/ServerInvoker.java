package org.jboss.fuse.qa.fafram8.environment;

import lombok.extern.slf4j.Slf4j;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.identity.Access;
import org.openstack4j.openstack.OSFactory;

/**
 * Thread worker class. OpenStack client is created with shared session from OpenStackClient singleton. Worker purpose
 * is spawn one single server per thread, wait for "active" status and register created server to OpenStackManager.
 *
 * Created by ecervena on 28.9.15.
 */
@Slf4j
public class ServerInvoker implements Runnable {

    //OpenStack client
    private OSClient os;

    //Name of the node
    private String nodeName;

    /**
     * Constructor for thread worker
     *
     * @param nodeName
     */
    public ServerInvoker(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * Method executed in thread
     */
    @Override
    public void run() {
        log.info("Creating server inside thread for container: " + nodeName);
        os = OSFactory.clientFromAccess(OpenStackClient.getInstance().getAccess());
        ServerCreate serverCreate = os
                .compute()
                .servers()
                .serverBuilder()
                .image("a61880d9-3cc3-40df-b172-d3282104adb4")
                .name("fafram8-" + nodeName)
                .flavor("3")
                .keypairName("ecervena")
                .build();
        //TODO(ecervena): do something smarter with server boot timeout
        Server server = os.compute().servers().bootAndWaitActive(serverCreate, 120000);
        OpenStackManager.registerServer(server);
    }
}
