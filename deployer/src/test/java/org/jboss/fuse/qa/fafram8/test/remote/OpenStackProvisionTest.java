package org.jboss.fuse.qa.fafram8.test.remote;

import org.jboss.fuse.qa.fafram8.environment.OpenStackManager;
import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.resource.Fafram;
import org.junit.*;

import static org.junit.Assert.assertTrue;

/**
 * Created by ecervena on 25.9.15.
 */
public class OpenStackProvisionTest {

    @BeforeClass
    public static void init() {
        System.setProperty(FaframConstant.FUSE_ZIP, "http://download.eng.bos.redhat.com/brewroot/repos/jb-fuse-6.2-build/latest/maven/org/jboss/fuse/jboss-fuse-full/6.2.0.redhat-133/jboss-fuse-full-6.2.0.redhat-133.zip");
    }

    @Rule
    public Fafram fafram = new Fafram().withFabric();

    @Test
    public void fabricTest() {
        assertTrue(fafram.executeFuseCommand("container-list | grep root").contains("success"));
        assertTrue(fafram.executeFuseCommand("container-list | grep node1").contains("success"));
    }

    @AfterClass
    public static void clean() {
        Fafram.getOsm().releaseResources();
    }
}
