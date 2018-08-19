/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data.tenant;

import java.util.List;

import io.goldfin.admin.service.api.model.Tag;
import io.goldfin.admin.service.api.model.TagSet;
import io.goldfin.shared.utilities.SerializationException;

/**
 * Extends TagSet to allow values to be added in a fluent programming style,
 * edit using Map set/remove operations, and implement equals() and clone() in
 * full form to enable testing.
 */
@SuppressWarnings("serial")
public class ExtendedTagSet extends TagSet implements Cloneable {
	/**
	 * Add tag and return instance instead of boolean used in collection classes.
	 */
	public ExtendedTagSet append(Tag tag) {
		this.add(tag);
		return this;
	}

	/**
	 * Make a deep copy so that clients can safely alter tags without affecting
	 * original instance contents.
	 */
	@Override
	public ExtendedTagSet clone() {
		ExtendedTagSet newObj = new ExtendedTagSet();
		for (Tag tag : this) {
			newObj.add(new Tag().name(tag.getName()).value(tag.getValue()));
		}
		return newObj;
	}

	/**
	 * Override generated equals() which does not correctly check Tags for equality,
	 * specifically that the list size, order, and contents are identical.
	 */
	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ExtendedTagSet otherTagSet = (ExtendedTagSet) o;
		// Compare the list
		if (this.size() != otherTagSet.size()) {
			return false;
		}
		for (int i = 0; i < this.size(); i++) {
			if (!this.get(i).equals(otherTagSet.get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Implement put with semantics of Map.put().
	 * <ol>
	 * <li>New name adds a tag results in addition of Tag</li>
	 * <li>Matching name updates the tag</li>
	 * <ol>
	 * 
	 * @param name
	 *            Key
	 * @param value
	 *            Corresponding value
	 * @throws NullPointerException
	 *             if name is null
	 */
	public void tagPut(String name, String value) {
		if (name == null) {
			throw new NullPointerException();
		}
		Tag foundTag = null;
		for (Tag tag : this) {
			if (name.equals(tag.getName())) {
				foundTag = tag;
				break;
			}
		}
		if (foundTag == null) {
			this.add(new Tag().name(name).value(value));
		} else {
			foundTag.setValue(value);
		}
	}

	/**
	 * Implement get with semantics of Map.get().
	 * 
	 * @param name
	 *            Key to find
	 * @return Value or null if name is not found
	 * @throws NullPointerException
	 *             if name is null
	 */
	public String tagGet(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		for (Tag tag : this) {
			if (name.equals(tag.getName())) {
				return tag.getValue();
			}
		}
		return null;
	}

	/**
	 * Implement delete with semantics of Map.delete(). A matching name deletes the
	 * tag while a non-matching tag does nothing.
	 * 
	 * @param name
	 *            Key to delete
	 * @return Tag value if found, otherwise null
	 * @throws NullPointerException
	 *             if name is null
	 */
	public String tagRemove(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		Tag foundTag = null;
		for (Tag tag : this) {
			if (name.equals(tag.getName())) {
				foundTag = tag;
				break;
			}
		}
		if (foundTag == null) {
			return null;
		} else {
			this.remove(foundTag);
			return foundTag.getValue();
		}
	}

	/**
	 * Generates an ExtendedTagSet from a list of strings of the form "name:value".
	 * 
	 * @param nameValueList
	 *            List of strings or null
	 * @return An ExtendedTagSet or null depending on input
	 * @throws SerializationException
	 */
	public static ExtendedTagSet fromNameValueList(List<String> nameValueList) {
		ExtendedTagSet tagSet = null;
		if (nameValueList != null) {
			tagSet = new ExtendedTagSet();
			for (String tagEntry : nameValueList) {
				String[] keyValuePair = tagEntry.trim().split(":");
				if (keyValuePair.length != 2 || keyValuePair[0].trim().length() == 0
						|| keyValuePair[1].trim().length() == 0) {
					throw new SerializationException(
							String.format("Tag does not have name:value format: %s", tagEntry));
				} else {
					tagSet.tagPut(keyValuePair[0].trim(), keyValuePair[1].trim());
				}
			}
		}
		return tagSet;
	}
}