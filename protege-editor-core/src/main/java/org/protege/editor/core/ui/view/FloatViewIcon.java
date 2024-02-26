package org.protege.editor.core.ui.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/02/2012
 */
public class FloatViewIcon extends ViewIcon {

    /**
     * Draw the icon at the specified location.  Icon implementations
     * may use the Component argument to get properties useful for
     * painting, e.g. the foreground or background color.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, getIconWidth(), getIconHeight());
        g.setColor(c.getBackground());
        g.fillRect(x + 1, y + 1, getIconWidth() - 2, getIconHeight() - 2);
        g.setColor(Color.WHITE);
        g.fillRect(x + 2, y + 2, getIconWidth() - 4, getIconHeight() - 4);
    }
}
