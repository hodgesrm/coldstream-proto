/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data.tenant.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.goldfin.admin.data.tenant.ExtendedTagSet;
import io.goldfin.admin.service.api.model.Tag;
import io.goldfin.shared.utilities.SerializationException;

/**
 * Verify operations on extended tag tests.
 */
public class ExtendedTagSetTest {
	/**
	 * Verify that clone() produces a distinct copy that is equal to the source but
	 * can be modified without changing the source contents.
	 */
	@Test
	public void testCloningAndEquals() throws Exception {
		ExtendedTagSet source = new ExtendedTagSet().append(new Tag().name("abc").value("123"));
		Assert.assertEquals("Equal to self", source, source);

		ExtendedTagSet clone1 = source.clone();
		Assert.assertEquals("Equal to unaltered clone", source, clone1);

		clone1.append(new Tag().name("def").value("456"));
		Assert.assertNotEquals("Unequal after tag is appended", source, clone1);

		ExtendedTagSet clone2 = source.clone();
		clone2.tagPut("abc", "789");
		Assert.assertNotEquals("Unequal after existing tag is altered", source, clone2);
	}

	/**
	 * Verify that we can put, get, and remove tags.
	 */
	@Test
	public void testPutGetRemove() throws Exception {
		ExtendedTagSet source = new ExtendedTagSet();
		Assert.assertNull("No tag available yet", source.tagGet("abc"));

		source.tagPut("abc", "123");
		Assert.assertEquals("Tag is present", "123", source.tagGet("abc"));

		source.tagPut("abc", "456");
		Assert.assertEquals("Tag is updated", "456", source.tagGet("abc"));

		Assert.assertEquals("Return deleted value", "456", source.tagRemove("abc"));
		Assert.assertNull("Tag is gone again", source.tagGet("abc"));
	}

	/**
	 * Verify that we can generate tags from well formed name:value lists but reject
	 * improperly constituted lists.
	 */
	@Test
	public void testNameValueDeserialization() throws Exception {
		// Prove that good values are accepted.
		ExtendedTagSet ts1 = ExtendedTagSet.fromNameValueList(asList("abc:123"));
		Assert.assertEquals(1, ts1.size());
		Assert.assertEquals("123", ts1.tagGet("abc"));

		ExtendedTagSet ts2 = ExtendedTagSet.fromNameValueList(asList("abc:123", "def:456"));
		Assert.assertEquals(2, ts2.size());
		Assert.assertEquals("123", ts2.tagGet("abc"));
		Assert.assertEquals("456", ts2.tagGet("def"));

		ExtendedTagSet ts3 = ExtendedTagSet.fromNameValueList(asList());
		Assert.assertEquals(0, ts3.size());

		ExtendedTagSet ts4 = ExtendedTagSet.fromNameValueList(null);
		Assert.assertNull(ts4);

		// Prove prove that bad values are rejected.
		for (String badValue : asList("abc:", ":bcd", "abc", ":", " : ")) {
			try {
				ExtendedTagSet badTagSet = ExtendedTagSet.fromNameValueList(asList(badValue));
				throw new Exception(
						String.format("Bad value accepted: value=%s tagSet=%s", badValue, badTagSet.toString()));
			} catch (SerializationException e) {
				// pass
			}
		}
	}

	private List<String> asList(String... varArgs) {
		return Arrays.asList(varArgs);
	}
}