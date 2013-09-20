package org.protege.editor.owl.ui.renderer;

import java.awt.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/02/2012
 */
public class OWLPropertyIcon extends OWLEntityIcon {

    private Color iconColor;

    public OWLPropertyIcon(Color iconColor) {
        super(SizeBias.EVEN);
        this.iconColor = iconColor;
    }

    /**
     * Draw the icon at the specified location.  Icon implementations
     * may use the Component argument to get properties useful for
     * painting, e.g. the foreground or background color.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        int xOffset = x + getPadding();
        int yOffset = y + getPadding();
        int width = getIconWidth() - 2 * getPadding();
        int height = getIconHeight() - 2 * getPadding();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color oldColor = g2.getColor();
        g2.setColor(Color.GRAY);
        int fillHeight = 2 * height / 3;
        int y1 = yOffset + height / 6 + OWLRendererPreferences.getInstance().getFontSize() / 10;
        g2.fillRoundRect(xOffset, y1, width, fillHeight, 4, 4);
        g2.setColor(iconColor);
        g2.fillRoundRect(xOffset + 1, y1 + 1, width - 2, fillHeight - 2, 4, 4);
        g2.setColor(oldColor);
    }
}
