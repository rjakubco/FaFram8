package org.jboss.fuse.qa.fafram8.test.remote;

import static org.junit.Assert.assertTrue;

import org.apache.commons.io.FileUtils;

import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.File;

/**
 * Created by avano on 8.10.15.
 */
public class RemoteJvmOptsTest {
	static {
		// TODO(avano,ecervena,rjakubco): change to openstack later
		System.setProperty(FaframConstant.HOST, "10.8.54.220");
		System.setProperty(FaframConstant.FUSE_ZIP, "http://download.eng.bos.redhat.com/brewroot/repos/jb-fsw-6" +
				".2-build/latest/maven/org/jboss/fuse/jboss-fuse-full/6.2.0.redhat-133/jboss-fuse-full-6.2.0.redhat-133.zip");
	}

	private Fafram fafram;

	@Test
	public void jvmOptsTest() throws Exception {
		fafram = new Fafram().suppressStart().setup();
		assertTrue(fafram.executeNodeCommand("cat " + System.getProperty(FaframConstant.FUSE_PATH) + File.separator + "bin" +
				File.separator + "setenv").contains("1536M"));
	}

	@Test
	public void customJvmOptsTest() throws Exception {
		fafram = new Fafram().setJvmOptions("312M", "512M", "312M", "512M").suppressStart().setup();
		assertTrue(fafram.executeNodeCommand("cat " + System.getProperty(FaframConstant.FUSE_PATH) + File.separator + "bin" +
				File.separator + "setenv").contains("312M"));
	}

	@After
	public void after() {
		if (fafram != null) {
			fafram.tearDown();
		}
	}

	@AfterClass
	public static void afterClass() {
		System.clearProperty(FaframConstant.HOST);
		System.clearProperty(FaframConstant.FUSE_ZIP);
	}
}
