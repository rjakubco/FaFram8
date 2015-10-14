package org.jboss.fuse.qa.fafram8.test.remote;

import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.provision.openstack.OpenStackProvisionProvider;
import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RemoteFabricTest {

    private final OpenStackProvisionProvider ospp = new OpenStackProvisionProvider();

    static {
        System.setProperty(FaframConstant.FUSE_ZIP, "file:///home/fuse/storage/jboss-fuse-full-6.2.0.redhat-133.zip");

    }

    @Rule
    public Fafram fafram = new Fafram().provideNodes(ospp).withFabric();

    //Uncomment ConfigurationParser.setDeployer(); in Fafaram to run remote deployment
    @Test
	@Ignore
    public void fabricTest() {
        assertTrue(fafram.executeCommand("container-list | grep root").contains("success"));
    }

	@AfterClass
	public static void clean() {
		System.clearProperty(FaframConstant.HOST);
		System.clearProperty(FaframConstant.FUSE_ZIP);
	}
}
