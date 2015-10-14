package org.jboss.fuse.qa.fafram8.test.remote;

import static org.junit.Assert.assertTrue;

import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;
import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RemotePropertiesTest {
	static {
		System.setProperty(FaframConstant.FUSE_ZIP, "http://download.eng.bos.redhat.com/brewroot/repos/jb-fuse-6.2-build/latest/maven/org/jboss/fuse/jboss-fuse-full/6.2.0.redhat-133/jboss-fuse-full-6.2.0.redhat-133.zip");
		System.setProperty(FaframConstant.HOST, RemoteTestSuite.ipAddress);
	}

	@Rule
	public Fafram fafram = new Fafram().modifyProperty("etc/custom.properties", "test.property", "testing", false);

	@Test
	public void testAddingProperty() {
		assertTrue(fafram.executeNodeCommand("cat " + SystemProperty.getFusePath() + File.separator + "etc" +
				File.separator + "custom.properties").contains("test.propety=testing"));
	}

	@AfterClass
	public static void clean() {
		System.clearProperty(FaframConstant.HOST);
		System.clearProperty(FaframConstant.FUSE_ZIP);
	}
}
