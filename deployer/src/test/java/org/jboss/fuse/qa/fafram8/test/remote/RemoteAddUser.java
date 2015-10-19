package org.jboss.fuse.qa.fafram8.test.remote;

import static org.junit.Assert.assertTrue;

import org.jboss.fuse.qa.fafram8.property.SystemProperty;
import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.Rule;
import org.junit.Test;

import java.io.File;

/**
 * Remote add user test.
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RemoteAddUser {

	@Rule
	public Fafram fafram = new Fafram().addUser("testu", "testp", "testr1,testr2").suppressStart();

	@Test
	public void testAddUser() {
		assertTrue(fafram.executeNodeCommand("cat " + SystemProperty.getFusePath() + File.separator + "etc" +
				File.separator + "users.properties").contains("testu=testp,testr1,testr2"));
	}
}
