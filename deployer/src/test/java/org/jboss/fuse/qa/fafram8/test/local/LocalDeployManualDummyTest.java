package org.jboss.fuse.qa.fafram8.test.local;

import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Local deploy manual test.
 * Created by avano on 19.8.15.
 */
public class LocalDeployManualDummyTest {
	private Fafram fafram;

	@Before
	public void init() {
		fafram = new Fafram();
		fafram.setup();
	}

	@Test
	public void dummyTest() {

	}

	@After
	public void tearDown() {
		fafram.tearDown();
	}
}
