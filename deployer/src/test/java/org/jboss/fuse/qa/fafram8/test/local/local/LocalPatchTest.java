package org.jboss.fuse.qa.fafram8.test.local.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.After;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * Local patch tests.
 * Created by avano on 2.9.15.
 */
@Slf4j
public class LocalPatchTest {
	private Fafram fafram;

	@Test
	public void karafLatestPatchTest() {
		System.setProperty("patch", "");
		log.info("System property is \'" + System.getProperty("patch") + "\'");
		fafram = new Fafram();
		fafram.setup();
		assertTrue(fafram.executeCommand("patch:list | grep jboss-fuse").contains("true"));
	}

	@Test
	public void karafFilePatchTest() {
		System.setProperty("patch", "file:///home/avano/work/patches/latest/jboss-fuse-6.2.0.redhat-153-p2.zip");
		log.info("System property is \'" + System.getProperty("patch") + "\'");
		fafram = new Fafram();
		fafram.setup();
		assertTrue(fafram.executeCommand("patch:list | grep jboss-fuse").contains("true"));
	}

	@Test
	public void karafHttpPatchTest() {
		System.setProperty("patch", "http://fuse-patches.gsslab.rdu2.redhat.com/patches/JBossFuse/6" +
				".2/p2/rc1/jboss-fuse-6.2.0.redhat-153-p2.zip");
		log.info("System property is \'" + System.getProperty("patch") + "\'");

		fafram = new Fafram();
		fafram.setup();
		assertTrue(fafram.executeCommand("patch:list | grep jboss-fuse").contains("true"));
	}

	@Test
	public void karafScpPatchTest() {

	}

	@Test
	public void karafStringPatchTest() {
		System.setProperty("patch", "p2");
		log.info("System property is \'" + System.getProperty("patch") + "\'");

		fafram = new Fafram();
		fafram.setup();
		assertTrue(fafram.executeCommand("patch:list | grep jboss-fuse").contains("true"));
	}

	@Test
	public void karafMultiplePatchesTest() {
		System.setProperty("patch", "p1,p2");
		log.info("System property is \'" + System.getProperty("patch") + "\'");

		fafram = new Fafram();
		fafram.setup();
		assertEquals("2", fafram.executeCommand("patch:list | grep -c true"));
	}

	@After
	public void after() {
		if (fafram != null) {
			fafram.tearDown();
		}
		System.clearProperty("patch");
	}
}
