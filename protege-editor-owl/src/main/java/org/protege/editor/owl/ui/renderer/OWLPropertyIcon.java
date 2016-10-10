package org.protege.editor.owl.ui.renderer;

import java.awt.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/02/2012
 */
public abstract class OWLPropertyIcon extends OWLEntityIcon {

    public static final BasicStroke HOLLOW_STROKE = new BasicStroke(2);

    private Color iconColor;

    public OWLPropertyIcon(Color iconColor, FillType fillType) {
        super(fillType);
        this.iconColor = iconColor;
    }

    /**
     * Draw the icon at the specified location.  Icon implementations
     * may use the Component argument to get properties useful for
     * painting, e.g. the foreground or background color.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {

        Graphics2D g2 = (Graphics2D) g.create();

        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

//            double scaleFactor = getScaleFactor();
//            g2.scale(scaleFactor, scaleFactor);

            // Icon is 16x16 with 2px padding round all sides

            int iconHeight = getBaseSize();

            int propWidth = getIconWidth();
            int propHeight = 8;
            int xPadding = (getIconWidth() - propWidth) / 2;
            int yPadding = (getIconHeight() - propHeight) / 2;
            int rX = x + xPadding;
            int rY = y + yPadding;

            if(getFillType() == FillType.FILLED) {
                g2.setColor(Color.GRAY);
                g2.fillRect(rX, rY, propWidth, propHeight);
                g2.setColor(getEntityColor());
                g2.fillRect(rX + 1, rY + 1, propWidth - 2, propHeight - 2);
            }
            else {
                g2.setStroke(HOLLOW_STROKE);
                g2.setColor(getEntityColor());
                g2.drawRect(rX + 1, rY + 1, propWidth - 2, propHeight - 2);
            }
        } finally {
            g2.dispose();
        }
    }
}
