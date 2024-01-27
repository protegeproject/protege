package org.protege.editor.owl.ui.renderer.layout;

import java.awt.Graphics2D;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 07/12/2011
 */
public class EmptyPageObjectBorder extends PageObjectBorder {

    private static final EmptyPageObjectBorder emptyBorder = new EmptyPageObjectBorder();


    public EmptyPageObjectBorder() {
        super(0, 0, 0, 0);
    }

    @Override
    protected void drawBorder(Graphics2D g2, int borderWidth, int borderHeight, PageObject pageObject) {
    }

    public static EmptyPageObjectBorder getEmptyPageObjectBorder() {
        return emptyBorder;
    }
}
