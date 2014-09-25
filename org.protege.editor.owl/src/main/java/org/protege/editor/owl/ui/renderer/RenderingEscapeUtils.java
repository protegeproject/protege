package org.protege.editor.owl.ui.renderer;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 20-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RenderingEscapeUtils {

    /**
     * Produces an "escaped" rendering.  If the original rendering contains
     * spaces, the returned value is the original rendering enclosed in
     * single quotes.
     * @param originalRendering The rendering to be escaped
     * @return The escaped rendering.
     */
    public static String getEscapedRendering(String originalRendering) {
        if (originalRendering.indexOf(' ') != -1 || originalRendering.indexOf('(') != -1 || originalRendering.indexOf(
                ')') != -1) {
            return "'" + originalRendering + "'";
        }
        else {
            return originalRendering;
        }
    }
}
