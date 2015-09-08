package org.jboss.fuse.qa.fafram8.test.remote;

import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RemoteFabricTest {

    static {
        System.setProperty(FaframConstant.HOST, "10.8.49.151");
//		System.setProperty(FaframConstant.FUSE_ZIP, "http://repository.jboss.org/nexus/content/groups/ea/org/jboss/fuse/jboss-fuse-full/6.2.1.redhat-020/jboss-fuse-full-6.2.1.redhat-020.zip");
        System.setProperty(FaframConstant.FUSE_ZIP, "file:///home/fuse/storage/jboss-fuse-full-6.2.0.redhat-133.zip");
    }

    @Rule
    public Fafram fafram = new Fafram().withFabric();

    @Test
    public void fabricTest() {
        assertTrue(fafram.executeFuseCommand("container-list | grep root").contains("success"));
    }

	@After
	public void clean() {
		System.clearProperty(FaframConstant.HOST);
		System.clearProperty(FaframConstant.FUSE_ZIP);
	}
}
