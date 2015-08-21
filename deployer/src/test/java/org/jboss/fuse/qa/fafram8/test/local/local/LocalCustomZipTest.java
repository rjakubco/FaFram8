package org.jboss.fuse.qa.fafram8.test.local.local;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.property.SystemProperty;
import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.After;
import org.junit.Test;

/**
 * Created by avano on 21.8.15.
 */
public class LocalCustomZipTest {
	private Fafram fafram;

	@Test
	public void customZipTest() {
		assertNotNull("Please specify " + FaframConstant.FUSE_ZIP + " property", SystemProperty.FUSE_ZIP);
		fafram = new Fafram();
		fafram.setup();

		// TODO(avano): rework based on file name
		// It sets the system property to the product path
		assertTrue("Does not contain expected dir in path",
				SystemProperty.FUSE_ZIP.contains("jboss-a-mq-6.1.0.redhat-379"));
	}

	@After
	public void after() {
		if (fafram != null) {
			fafram.tearDown();
		}
	}
}
