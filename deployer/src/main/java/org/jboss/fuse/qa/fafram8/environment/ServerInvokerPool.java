package org.jboss.fuse.qa.fafram8.environment;

import org.jboss.fuse.qa.fafram8.manager.Container;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

/**
 * ServerInvoker thread pool with thread executor. One thread per container will be spawn.
 * <p/>
 * Created by ecervena on 28.9.15.
 */
@Slf4j
public class ServerInvokerPool {

	/**
	 * Calling this methond will spawn thread workers to create OpenStack nodes in parallel.
	 *
	 * @param containers list of containers provided by ConfigurationParser
	 */
	public static void spawnServers(List<Container> containers) {
		log.info("Initializing ServerInvokerPool.");
		//TODO(ecervena): 5 threads in pool is only for proof of concept purposes. Figure out something smarter.
		final ExecutorService executor = Executors.newFixedThreadPool(6);
		for (Container container : containers) {
			log.info("Spawning invoker thread for container: " + container.getName());
			final Runnable serverInvoker = new ServerInvoker(container.getName());
			executor.execute(serverInvoker);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		log.info("ServerInvokerPool done.");
	}
}
