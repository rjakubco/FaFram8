package org.jboss.fuse.qa.fafram8.test.common;

import org.jboss.fuse.qa.fafram8.exception.ValidatorException;
import org.jboss.fuse.qa.fafram8.property.FaframConstant;
import org.jboss.fuse.qa.fafram8.resource.Fafram;

import org.junit.After;
import org.junit.Test;

/**
 * Patch validator tests.
 * Created by avano on 22.9.15.
 */
public class PatchValidatorTest {
	@Test(expected = ValidatorException.class)
	public void nonExistentPatchNameValidationTest() {
		System.setProperty(FaframConstant.PATCH, "nonexistent");
		new Fafram();
	}

	@Test(expected = ValidatorException.class)
	public void fuseEmptyHostValidationTest() {
		System.setProperty(FaframConstant.PATCH, "file:///nonexistent");
		new Fafram();
	}

	@After
	public void after() {
		System.clearProperty(FaframConstant.PATCH);
	}
}
