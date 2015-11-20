package org.protege.editor.owl.ui.framelist;

import javax.swing.border.Border;
import java.awt.*;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Dec-2007<br><br>
 */
public class OWLFrameListInferredSectionRowBorder implements Border {

    /**
     * Paints the border for the specified component with the specified
     * position and size.
     * @param c      the component for which this border is being painted
     * @param g      the paint graphics
     * @param x      the x position of the painted border
     * @param y      the y position of the painted border
     * @param width  the width of the painted border
     * @param height the height of the painted border
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        // Nothing to paint anymore.  See #277 on github.  We keep the border incase third parties
        // use it.  Also, insets are preserved.
    }


    /**
     * Returns the insets of the border.
     * @param c the component for which this border insets value applies
     */
    public Insets getBorderInsets(Component c) {
        return new Insets(1, 1, 1, 1);
    }


    /**
     * Returns whether or not the border is opaque.  If the border
     * is opaque, it is responsible for filling in it's own
     * background when painting.
     */
    public boolean isBorderOpaque() {
        return false;
    }
}
