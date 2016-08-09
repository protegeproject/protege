package org.protege.editor.owl.ui.renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 9 Aug 16
 */
public class AddChildIcon implements Icon {

    private final OWLEntityIcon entityIcon;

    public AddChildIcon(OWLEntityIcon entityIcon) {
        this.entityIcon = entityIcon;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        Color oldColor = g.getColor();

        int cX0 = x + getIconWidth() / 4;
        int cY0 = y + getIconHeight() / 4;

        int cX1 = x - 1 +  (getIconWidth() * 3) / 4;
        int cY1 = y - 1 + (getIconHeight() * 3) / 4;

        g.setColor(entityIcon.getEntityColor());

        g.drawLine(cX0, cY0, cX0, cY1);
        g.drawLine(cX0, cY1, cX1, cY1);

        int addX = x + getIconWidth() - 4;
        int addY = y + 4;
        int addLegLen = 2;
        g.drawLine(addX - addLegLen, addY, addX + addLegLen, addY);
        g.drawLine(addX, addY - addLegLen, addX, addY + addLegLen);

        g.setColor(oldColor);

        double scaleFactor = 0.8;
        g.translate(cX0, cY0);
        int parX = -entityIcon.getIconWidth() / 2;
        int parY = -entityIcon.getIconHeight() / 2;

        g2.scale(scaleFactor, scaleFactor);
        entityIcon.paintIcon(c, g, parX, parY);
        g2.scale(1 / scaleFactor, 1 / scaleFactor);
        g.translate(-cX0, -cY0);

        g.translate(cX1, cY1);
        int childX = -entityIcon.getIconWidth() / 2;
        int childY = -entityIcon.getIconHeight() / 2;
        g2.scale(scaleFactor, scaleFactor);
        entityIcon.paintIcon(c, g, childX, childY);
        g2.scale(1 / scaleFactor, 1 / scaleFactor);
        g.translate(-cX1, -cY1);

    }

    @Override
    public int getIconWidth() {
        return entityIcon.getIconWidth() + 2;
    }

    @Override
    public int getIconHeight() {
        return entityIcon.getIconHeight() + 2;
    }
}
