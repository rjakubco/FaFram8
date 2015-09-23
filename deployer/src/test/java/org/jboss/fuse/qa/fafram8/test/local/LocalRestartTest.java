package org.jboss.fuse.qa.fafram8.test.local;

import static org.junit.Assert.assertTrue;

import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.After;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * Local restart test class.
 * Created by avano on 23.9.15.
 */
@Slf4j
public class LocalRestartTest {
	private Fafram fafram;

	@Test
	public void restartTest() throws Exception {
		fafram = new Fafram();
		fafram.setup();
		log.info("Sleeping for 120sec");
		Thread.sleep(120000L);
		assertTrue(fafram.executeCommand("shell:info | grep Uptime").contains("2 minutes"));
		fafram.restart();
		assertTrue(fafram.executeCommand("shell:info | grep Uptime").contains("seconds"));
	}

	@After
	public void after() {
		if (fafram != null) {
			fafram.tearDown();
		}
	}
}
