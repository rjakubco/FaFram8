package org.jboss.fuse.qa.fafram8.test.local;

import static org.junit.Assert.assertTrue;

import org.apache.commons.io.FileUtils;

import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Local add user test.
 * Created by avano on 21.8.15.
 */
public class LocalAddUserTest {
	@Rule
	public Fafram fafram = new Fafram().addUser("testu", "testp", "testr1,testr2");

	@Test
	public void userTest() throws IOException {
		String fileContent = FileUtils.readFileToString(new File(System.getProperty(FaframConstant.FUSE_PATH) + File.separator + "etc" +
				File.separator + "users.properties"));
		assertTrue(fileContent.contains("testu=testp,testr1,testr2"));
	}
}
