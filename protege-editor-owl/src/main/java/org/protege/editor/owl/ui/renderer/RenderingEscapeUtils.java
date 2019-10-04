package org.protege.editor.owl.ui.renderer;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


import javax.annotation.Nonnull;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 20-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RenderingEscapeUtils {

    public enum RenderingEscapeSetting {
        ESCAPED_RENDERING,
        UNESCAPED_RENDERING
    }

    /**
     * Produces an "escaped" rendering.  If the original rendering contains
     * spaces, braces, brackets or commas, and various other symbols used as delimeters in the
     * Manchester syntax parser then the returned value is the original rendering enclosed in
     * single quotes.
     * @param originalRendering The rendering to be escaped
     * @return The escaped rendering.
     */
    public static String getEscapedRendering(String originalRendering) {
        originalRendering = originalRendering.replace("'", "\\'");
        if (originalRendering.indexOf(' ') != -1
                || originalRendering.indexOf(',') != -1
                || originalRendering.indexOf('<') != -1
                || originalRendering.indexOf('>') != -1
                || originalRendering.indexOf('=') != -1
                || originalRendering.indexOf('^') != -1
                || originalRendering.indexOf('@') != -1
                || originalRendering.indexOf('{') != -1
                || originalRendering.indexOf('}') != -1
                || originalRendering.indexOf('[') != -1
                || originalRendering.indexOf(']') != -1
                || originalRendering.indexOf('(') != -1
                || originalRendering.indexOf(')') != -1) {
            return "'" + originalRendering + "'";
        }
        else {
            return originalRendering;
        }
    }

    @Nonnull
    public static String unescape(@Nonnull String rendering) {
        rendering = rendering.replace("\\'", "'");
        if(rendering.startsWith("'") && rendering.endsWith("'")) {
            return rendering.substring(1, rendering.length() - 1);
        }
        else {
            return rendering;
        }
    }

}
