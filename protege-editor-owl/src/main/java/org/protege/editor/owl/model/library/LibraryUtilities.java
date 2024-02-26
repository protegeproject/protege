package org.protege.editor.owl.model.library;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.protege.xmlcatalog.entry.Entry;

public class LibraryUtilities {
	public static final String VERSION_PROPERTY = "version";
	public static final int DEFAULT_VERSION = 0;
    public static final String AUTO_UPDATE_PROP = "Auto-Update";
	
    public static String getStringProperty(Entry entry, String property) {
		if (entry.getId() == null) {
			return null;
		}
		String s = entry.getId();
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
    
    public static boolean getBooleanProperty(Entry entry, String  property, boolean defaultValue) {
    	String s  = getStringProperty(entry, property);
    	if (s == null) {
    		return defaultValue;
    	}
    	else if (s.toLowerCase().equals("true")) {
    		return true;
    	}
    	else if (s.toLowerCase().equals("false")) {
    		return false;
    	}
    	else return defaultValue;
    }
    
    public static int getIntegerProperty(Entry entry, String property, int defaultValue) {
        String  s = getStringProperty(entry, property);
        if (s == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(s);
        }
        catch (Throwable t) {
            return defaultValue;
        }
    }
    
    public static int getVersion(Entry entry) {
        return getIntegerProperty(entry, VERSION_PROPERTY, DEFAULT_VERSION);
    }
    
    public static void addPropertyValue(StringBuffer sb, String property, Object value) {
        sb.append(", ");
        sb.append(property);
        sb.append("=");
        sb.append(value);
    }


}
