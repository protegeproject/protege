package org.protege.editor.owl.ui.renderer;

import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 9 Aug 16
 */
public class AddEntityIcon implements Icon {

    private final OWLEntityIcon entityIcon;

    public AddEntityIcon(OWLEntityIcon entityIcon) {
        this.entityIcon = entityIcon;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            EntityActionIcon.setupAlpha(c, g2);

            entityIcon.paintIcon(c, g2, x + 1, y + 1);
            int addCrossLegLength = 2;
            int xC = x + entityIcon.getIconWidth() + addCrossLegLength;
            int yC = y + 4;
            g2.setStroke(EntityActionIcon.ACTION_STROKE);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setColor(entityIcon.getEntityColor());
            g2.drawLine(xC - addCrossLegLength, yC, xC + addCrossLegLength, yC);
            g2.drawLine(xC, yC - addCrossLegLength, xC, yC + addCrossLegLength);
        } finally {
            g2.dispose();
        }

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
