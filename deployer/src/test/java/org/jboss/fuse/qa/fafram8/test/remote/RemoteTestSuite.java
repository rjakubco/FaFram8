package org.jboss.fuse.qa.fafram8.test.remote;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */

import org.jboss.fuse.qa.fafram8.provision.openstack.OpenStackProvisionProvider;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		RemoteFabricTest.class
})
public class RemoteTestSuite {
	public static final String SERVER_NAME = "FaframRemoteTestNode";

	public static String ipAddress;

	private static OpenStackProvisionProvider osm = new OpenStackProvisionProvider();

	@BeforeClass
	public static void before(){
		osm.spawnNewServer(SERVER_NAME);

		ipAddress = osm.assignFloatingAddress(osm.getServerByName(SERVER_NAME).getId());
	}

	@AfterClass
	public static void after(){
		osm.deleteServer(SERVER_NAME);
	}

}
