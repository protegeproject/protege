package org.protege.editor.core.prefs;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class PreferencesTest {

	@Test
	public void testClearStringList() {
		PreferencesManager prefMan = PreferencesManager.getInstance();
		Preferences prefs = prefMan.getPreferencesForSet(
				"someRandomSetId#1973948103", "somePrefsId");
		prefs.clear(); // just in case there are values from the previous run
		String stringListKey = "someKey";
		List<String> defaultList = Arrays.asList("A", "B");
		List<String> list = prefs.getStringList(stringListKey, defaultList);
		assertTrue(list.equals(defaultList));

		List<String> newList = Arrays.asList("One", "Two", "Tree");
		prefs.putStringList(stringListKey, newList);

		list = prefs.getStringList(stringListKey,
				Collections.<String> emptyList());
		assertTrue(list.equals(newList));

		prefs.clear();
		list = prefs.getStringList(stringListKey, defaultList);
		assertTrue(list.equals(defaultList));
	}
}
