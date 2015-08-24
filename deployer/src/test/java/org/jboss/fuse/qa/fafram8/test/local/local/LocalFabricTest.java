package org.jboss.fuse.qa.fafram8.test.local.local;

import static org.junit.Assert.assertTrue;

import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.Rule;
import org.junit.Test;

/**
 * Local fabric test.
 * Created by avano on 21.8.15.
 */
public class LocalFabricTest {
	@Rule
	public Fafram fafram = new Fafram().withFabric();

	@Test
	public void fabricTest() {
		assertTrue(fafram.executeCommand("container-list | grep root").contains("success"));
	}
}
