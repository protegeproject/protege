package org.protege.editor.owl.ui.renderer.layout;

import java.awt.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 07/12/2011
 */
public class SolidPageObjectBorder extends PageObjectBorder {

    private Color borderColor;

    public SolidPageObjectBorder(int topInset, int bottomInset, int leftInset, int rightInset, Color borderColor) {
        super(topInset, bottomInset, leftInset, rightInset);
        this.borderColor = borderColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    @Override
    protected void drawBorder(Graphics2D g2, int borderWidth, int borderHeight, PageObject pageObject) {
        Color oldColor = g2.getColor();
        g2.setColor(borderColor);
        g2.fillRect(0, 0, getLeftInset(), borderHeight);
        g2.fillRect(borderWidth - getRightInset(), 0, getRightInset(), borderHeight);
        g2.fillRect(0, 0, borderWidth, getTopInset());
        g2.fillRect(0, borderHeight - getBottomInset(), borderWidth, getBottomInset());
        g2.setColor(oldColor);
    }
}
