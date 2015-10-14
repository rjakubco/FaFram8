package org.jboss.fuse.qa.fafram8.test.remote;

import static org.junit.Assert.assertTrue;

import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * todo(rjakubco): Dopisat test
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RemoteOnlyConnectTest {
	static {
		// TODO(ecervena): machine is dead
		System.setProperty(FaframConstant.HOST, "10.8.48.79");
		System.setProperty(FaframConstant.FUSE_ZIP, "file:///home/fuse/storage/jboss-fuse-full-6.2.0.redhat-133.zip");
	}

	@Rule
	public Fafram fafram = new Fafram().withFabric();

	@Test
	public void fabricTest() {
		Fafram fafram2 =  new Fafram().onlyConnect();

		String response = fafram2.executeCommand("container-list");
		assertTrue(response.contains("root"));
		assertTrue(response.contains("success"));
	}

	@AfterClass
	public static void clean() {
		System.clearProperty(FaframConstant.HOST);
		System.clearProperty(FaframConstant.FUSE_ZIP);
	}
}
