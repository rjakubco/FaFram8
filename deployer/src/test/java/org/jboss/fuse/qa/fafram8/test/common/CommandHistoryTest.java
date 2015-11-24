package org.jboss.fuse.qa.fafram8.test.common;

import static org.junit.Assert.assertTrue;

import org.apache.commons.io.FileUtils;

import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

/**
 * Command history test.
 * Created by avano on 24.11.15.
 */
public class CommandHistoryTest {
	public Fafram fafram = new Fafram();

	@Test
	public void commandHistoryTest() throws Exception {
		fafram.setup();
		fafram.executeCommand("echo hello");
		fafram.executeCommand("echo hi");
		fafram.tearDown();
		File path = new File(Paths.get("target", "archived").toAbsolutePath().toString());
		String fileName = path.list()[path.list().length - 1];
		String content = FileUtils.readFileToString(new File(Paths.get("target", "archived", fileName).toAbsolutePath().toString()));
		System.out.println(fileName);
		assertTrue(content.contains("echo hello"));
		assertTrue(content.contains("echo hi"));
	}
}
