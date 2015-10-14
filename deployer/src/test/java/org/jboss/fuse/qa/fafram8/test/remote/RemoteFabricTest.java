package org.jboss.fuse.qa.fafram8.test.remote;

import static org.junit.Assert.assertTrue;

import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RemoteFabricTest {

    static {
        System.setProperty(FaframConstant.FUSE_ZIP, "http://download.eng.bos.redhat.com/brewroot/repos/jb-fuse-6.2-build/latest/maven/org/jboss/fuse/jboss-fuse-full/6.2.0.redhat-133/jboss-fuse-full-6.2.0.redhat-133.zip");
        System.setProperty(FaframConstant.HOST, RemoteTestSuite.ipAddress);
    }

    @Rule
    public Fafram fafram = new Fafram().withFabric();

    @Test
	public void fabricTest() {
        assertTrue(fafram.executeCommand("container-list | grep root").contains("success"));
    }

	@AfterClass
	public static void clean() {
		System.clearProperty(FaframConstant.HOST);
		System.clearProperty(FaframConstant.FUSE_ZIP);
	}
}
