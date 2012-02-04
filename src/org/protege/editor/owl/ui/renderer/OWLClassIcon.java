package org.protege.editor.owl.ui.renderer;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/02/2012
 */
public class OWLClassIcon extends OWLEntityIcon {

    public static final Color COLOR = OWLSystemColors.getOWLClassColor();

    private Type type;

    public enum Type {
        PRIMITIVE,
        DEFINED
    }


    public OWLClassIcon(Type type) {
        this.type = type;
    }

    /**
     * Draw the icon at the specified location.  Icon implementations
     * may use the Component argument to get properties useful for
     * painting, e.g. the foreground or background color.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.translate(x + getPadding(), y + getPadding());


        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color oldColor = g.getColor();
        g.setColor(Color.GRAY);
        int size = getSize() - 2 * getPadding();
        g.fillOval(x, y, size, size);
        g.setColor(COLOR);

        g.fillOval(x + 1, y + 1, size - 2, size - 2);
        if (type.equals(Type.PRIMITIVE)) {
            int strokeWidth = Math.round(1 + OWLRendererPreferences.getInstance().getFontSize() / 10);
            g2.setColor(Color.WHITE);
            g.fillOval(x + strokeWidth + 1, y + strokeWidth + 1, size - strokeWidth * 2 - 2, size - strokeWidth * 2 - 2);
        }

        g.setColor(oldColor);

        g.translate(-x - getPadding(), -y - getPadding());

    }
}
