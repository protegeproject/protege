package org.protege.editor.owl.model.prefix;

import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 19 Oct 2017
 */
public class Prefix {

    public static final Pattern PN_CHARS_BASE =
            Pattern.compile(
                    "[A-Z]" +
                            "|[a-z]" +
                            "|[\u00C0-\u00D6]" +
                            "|[\u00D8-\u00F6]" +
                            "|[\u00F8-\u02FF]" +
                            "|[\u0370-\u037D]" +
                            "|[\u037F-\u1FFF]" +
                            "|[\u200C-\u200D]" +
                            "|[\u2070-\u218F]" +
                            "|[\u2C00-\u2FEF]" +
                            "|[\u3001-\uD7FF]" +
                            "|[\uF900-\uFDCF]" +
                            "|[\uFDF0-\uFFFD]" +
                            "|[\u10000-\uEFFFF]"
            );

    public static final Pattern PN_CHARS_U =
            Pattern.compile(
                    PN_CHARS_BASE.pattern() +
                            "|_"
            );

    public static final Pattern PN_CHARS =
            Pattern.compile(
                    PN_CHARS_U.pattern() +
                            "|-" +
                            "|[0-9]" +
                            "|\u00B7" +
                            "|[\u0300-\u036F]" +
                            "|[\u203F-\u2040]"
            );

    /**
     * The pattern for the PN_PREFIX non-terminal.  This is one start character followed by a string of
     * zero or more characters that ends with a non-period character.
     */
    public static final Pattern PN_PREFIX = Pattern.compile(
            String.format("(%s)(((%s)|\\.)*(%s))?",
                          PN_CHARS_BASE.pattern(),
                          PN_CHARS.pattern(),
                          PN_CHARS.pattern())
    );


    /**
     * The pattern for a prefix name.  This is an optional PN_PREFIX followed by a mandatory colon.
     */
    public static final Pattern PREFIX_NAME = Pattern.compile(
            String.format("(%s)?:", PN_PREFIX.pattern())
    );

    public static final Pattern HEX = Pattern.compile("[0-9]|[A-F]|[a-f]");

    public static final Pattern PN_LOCAL_ESC = Pattern.compile(
            "\\\\(_|~|.|\\-|!|\\$|&|'|\\(|\\)|\\*|\\+|,|;|=|\\/|\\?|\\#|@|%)"
    );

    public static final Pattern PLX = Pattern.compile(
            String.format("%%%s%s|%s",
                          HEX.pattern(),
                          HEX.pattern(),
                          PN_LOCAL_ESC.pattern())
    );


    public static final Pattern PN_LOCAL = Pattern.compile(
        String.format("(%s|:|[0-9]|%s)((%s|\\.|:|%s)*(%s|:|%s))?",
                      PN_CHARS_U.pattern(),
                      PLX.pattern(),
                      PN_CHARS.pattern(),
                      PLX.pattern(),
                      PN_CHARS.pattern(),
                      PLX.pattern())
    );


}
