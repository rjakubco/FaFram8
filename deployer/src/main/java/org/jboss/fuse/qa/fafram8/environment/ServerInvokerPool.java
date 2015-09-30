package org.jboss.fuse.qa.fafram8.environment;

import lombok.extern.slf4j.Slf4j;
import org.jboss.fuse.qa.fafram8.manager.Container;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.identity.Access;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ServerInvoker thread pool with thread executor. One thread per container will be spawn.
 *
 * Created by ecervena on 28.9.15.
 */
@Slf4j
public class ServerInvokerPool {

    /**
     * Calling this methond will spawn thread workers to create OpenStack nodes in parallel.
     *
     * @param containers list of containers provided by ConfigurationParser
     * @param access shared OpenStackClient authentication token
     */
    public static void spawnServers(List<Container> containers, Access access) {
        log.info("Initializing ServerInvokerPool.");
        //TODO(ecervena): 5 threads in pool is only for proof of concept purposes. Figure out something smarter.
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for(Container container: containers) {
            log.info("Spawning invoker thread for container: " + container.getName());
            Runnable serverInvoker = new ServerInvoker(container.getName());
            executor.execute(serverInvoker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
        log.info("ServerInvokerPool done.");

    }
}
