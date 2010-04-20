package org.protege.editor.owl.model.library;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.protege.xmlcatalog.entry.Entry;

public class LibraryUtilities {
    public static String getStringProperty(Entry entry, String property) {
		if (entry.getId() == null) {
			return null;
		}
		String s = entry.getId();
		if (s == null) {
		    return null;
		}
		int flags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
		Pattern inMiddle = Pattern.compile(", " + property + "=([^,]*),", flags);
		Matcher m1 = inMiddle.matcher(s);
		if (m1.find()) {
			return m1.group(1);
		}
		Pattern atEnd = Pattern.compile(", " + property + "=(.*)$", flags);
		Matcher m2 = atEnd.matcher(s);
		if (m2.find()) {
			return m2.group(1);
		}
		return null;
	}

}
