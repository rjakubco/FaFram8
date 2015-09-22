package org.jboss.fuse.qa.fafram8.test.common;

import org.jboss.fuse.qa.fafram8.exception.ValidatorException;
import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.After;
import org.junit.Test;

/**
 * Host validator tests.
 * Created by avano on 22.9.15.
 */
public class HostValidatorTest {
	@Test(expected = ValidatorException.class)
	public void nonExistentHostNameValidationTest() {
		System.setProperty(FaframConstant.HOST, "1.2.3.4");
		new Fafram();
	}

	@Test(expected = ValidatorException.class)
	public void emptyHostValidationTest() {
		System.setProperty(FaframConstant.HOST, "");
		new Fafram();
	}

	@After
	public void after() {
		System.clearProperty(FaframConstant.HOST);
	}
}
