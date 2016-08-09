package org.protege.editor.owl.ui.renderer;

import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 9 Aug 16
 */
public class OWLDatatypeIcon extends OWLEntityIcon {


    public static final Color COLOR = OWLSystemColors.getOWLDatatypeColor();

    public OWLDatatypeIcon() {
        super(OWLEntityIcon.SizeBias.EVEN);
    }

    /**
     * Draw the icon at the specified location.  Icon implementations
     * may use the Component argument to get properties useful for
     * painting, e.g. the foreground or background color.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.translate(getPadding(), getPadding());

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color oldColor = g.getColor();
        g.setColor(Color.LIGHT_GRAY);
        int size = getSize() - 2 * getPadding();
        g.fillOval(x, y, size, size);
        g.setColor(COLOR);

        g.fillOval(x + 1, y + 1, size - 2, size - 2);

        g.setColor(oldColor);

        g.translate(-getPadding(), -getPadding());

    }
}
