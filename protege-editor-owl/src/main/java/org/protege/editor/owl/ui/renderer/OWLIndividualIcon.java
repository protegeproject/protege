package org.protege.editor.owl.ui.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/02/2012
 */
public class OWLIndividualIcon extends OWLEntityIcon {

    private static final Color COLOR = OWLSystemColors.getOWLIndividualColor();

    public static final BasicStroke HOLLOW_STROKE = new BasicStroke(2);

    public OWLIndividualIcon() {
        this(FillType.FILLED);
    }

    public OWLIndividualIcon(FillType fillType) {
        super(fillType);
    }

    @Override
    public Color getEntityColor() {
        return COLOR;
    }

    /**
     * Draw the icon at the specified location.  Icon implementations
     * may use the Component argument to get properties useful for
     * painting, e.g. the foreground or background color.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            int width = 12;
            int height = 12;

            int xOffset = x + getIconWidth() / 2 - width / 2;
            int yOffset = y + getIconHeight() / 2 - height / 2;


            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.rotate(Math.PI / 4, x + getIconWidth() / 2, y + getIconHeight() / 2);
            if(getFillType() == FillType.FILLED) {
                g2.setColor(getBorderColor());
                g2.fillRoundRect(xOffset, yOffset, width, height, 4, 4);
                g2.setColor(getColor());
                g2.fillRoundRect(xOffset + 1, yOffset + 1, width - 2, height - 2, 4, 4);
            }
            else {
                g2.setStroke(HOLLOW_STROKE);
                g2.setColor(getColor());
                g2.drawRoundRect(xOffset + 1, yOffset + 1, width - 2, height - 2, 4, 4);

            }
        } finally {
            g2.dispose();
        }

    }
}
