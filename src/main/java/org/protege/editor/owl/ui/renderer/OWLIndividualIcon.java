package org.protege.editor.owl.ui.renderer;

import java.awt.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/02/2012
 */
public class OWLIndividualIcon extends OWLEntityIcon {

    private static final Color COLOR = OWLSystemColors.getOWLIndividualColor();

    public OWLIndividualIcon() {
        super(SizeBias.EVEN);
    }

    /**
     * Draw the icon at the specified location.  Icon implementations
     * may use the Component argument to get properties useful for
     * painting, e.g. the foreground or background color.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        int xOffset = x + getPadding() + 1;
        int yOffset = y + getPadding() + 1;
        int width = getSize() - getPadding() * 2 - 2;
        int height = getSize() - getPadding() * 2 - 2;
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.rotate(Math.PI / 4, x + getSize() / 2, y + getSize() / 2);
        g2.setColor(Color.GRAY);
        g2.fillRoundRect(xOffset, yOffset, width, height, 4, 4);
        g2.setColor(COLOR);
        g2.fillRoundRect(xOffset + 1, yOffset + 1, width - 2, height - 2, 4, 4);
        
    }
}
