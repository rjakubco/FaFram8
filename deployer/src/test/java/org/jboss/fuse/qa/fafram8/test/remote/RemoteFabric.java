package org.jboss.fuse.qa.fafram8.test.remote;

import static org.junit.Assert.assertTrue;

import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.Rule;
import org.junit.Test;

/**
 * Remote fabric test.
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RemoteFabric {

	@Rule
	public Fafram fafram = new Fafram().withFabric();

	@Test
	public void fabricTest() {
		assertTrue(fafram.executeCommand("container-list | grep root").contains("success"));
	}
}
