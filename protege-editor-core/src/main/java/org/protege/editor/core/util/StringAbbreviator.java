package org.protege.editor.core.util;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2 Jun 16
 */
public class StringAbbreviator {

    public static final String ELLIPSIS = "\u2026";

    /**
     * Abbreviates the specified string to the specified length
     * @param s The String to be abbreviated.  Not {@code null}.
     * @param length The maximum length.  The original string will be truncated to this length.
     * @return The abbreviated string that is a length less than or equal the specified length.  If the specified length
     * is less than the original string length then a trailing ellipsis will be added.
     */
    public static String abbreviateString(String s, int length) {
        if(s == null) {
            return null;
        }
        if(s.isEmpty()) {
            return s;
        }
        if(length <= 0) {
            return ELLIPSIS;
        }
        // Finish at either the length or the specified length
        int endIndex = Math.min(s.length(), length);
        String truncatedString = s.substring(0, endIndex);
        if(truncatedString.length() < s.length()) {
            return truncatedString + ELLIPSIS;
        }
        else {
            return truncatedString;
        }
    }

}
