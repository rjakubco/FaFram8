package org.jboss.fuse.qa.fafram8.test.local;

import static org.junit.Assert.assertTrue;

import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * Local fabric test.
 * Created by avano on 21.8.15.
 */
public class LocalFabricTest {

	@BeforeClass
	public static void init() {
		System.setProperty(FaframConstant.LOCAL_DEPLOYMENT, "true");
		System.setProperty(FaframConstant.FUSE_ZIP, "file:/home/ecervena/fuse/build/jboss-fuse-full-6.2.0.redhat-133.zip");
	}

	@Rule
	public Fafram fafram = new Fafram().withFabric();

	@Test
	public void fabricTest() {
		assertTrue(fafram.executeCommand("container-list | grep root").contains("success"));
	}
}
