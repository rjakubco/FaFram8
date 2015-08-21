package org.jboss.fuse.qa.fafram8.test.local.local;

import static org.junit.Assert.assertTrue;

import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created by avano on 21.8.15.
 */
public class LocalKeepFolderTest {
	private Fafram fafram;

	@Before
	public void before() {
		System.setProperty("keepFolder", "true");
	}

	@Test
	public void keepFolderTest() {
		fafram = new Fafram();
		fafram.setup();

		// It sets the system property to the product path
		String path = System.getProperty(FaframConstant.FUSE_PATH);
		fafram.tearDown();

		assertTrue("Folder was deleted", new File(path).exists());
	}

	@After
	public void after() {
		if (fafram != null) {
			fafram.tearDown();
		}
	}
}
