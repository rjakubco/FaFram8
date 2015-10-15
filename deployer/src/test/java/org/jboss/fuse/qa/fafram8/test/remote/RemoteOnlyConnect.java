package org.jboss.fuse.qa.fafram8.test.remote;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * Remote onlyConnect property test
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
@Slf4j
public class RemoteOnlyConnect {

	@ClassRule
	public static Fafram fafram = new Fafram();

	@Test
	public void onlyConnectTest() {
		Fafram fafram2 = new Fafram().onlyConnect().setup();

		String response = fafram2.executeCommand("osgi:list -t 0 | grep \"Apache Karaf :: Shell :: Console\"");
		System.out.println("response : " + response);
		assertNotNull(response);
		assertTrue(response.contains("Apache Karaf :: Shell :: Console"));
		assertTrue(response.contains("[Active"));
		assertTrue(response.contains("[Created"));
	}

	@Test
	public void cleanPropetyTest() {
		System.setProperty(FaframConstant.CLEAN, "false");
		Fafram fafram2 = new Fafram().onlyConnect().setup();

		String response = fafram2.executeCommand("osgi:list -t 0 | grep \"Apache Karaf :: Shell :: Console\"");
		System.out.println("response : " + response);
		assertNotNull(response);
		assertTrue(response.contains("Apache Karaf :: Shell :: Console"));
		assertTrue(response.contains("[Active"));
		assertTrue(response.contains("[Created"));
	}

	@After
	public void tearDown() throws Exception {
		System.clearProperty(FaframConstant.CLEAN);
	}
}
