package org.protege.editor.owl.ui.renderer;

import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 9 Aug 16
 */
public class DeleteEntityIcon implements Icon {


    private final OWLEntityIcon entityIcon;

    public DeleteEntityIcon(OWLEntityIcon entityIcon) {
        this.entityIcon = entityIcon;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            entityIcon.paintIcon(c, g2, x + 1, y + 1);
            int xC = getIconWidth() / 2 + x;
            int yC = getIconHeight() / 2 + y;
            g2.setStroke(EntityActionIcon.ACTION_STROKE);
            g2.setColor(entityIcon.getEntityColor());
            int crossLegLen = 7;
            g2.drawLine(xC - crossLegLen, yC - crossLegLen, xC + crossLegLen, yC + crossLegLen);
            g2.drawLine(xC - crossLegLen, yC + crossLegLen, xC + crossLegLen, yC - crossLegLen);

        } finally {
            g2.dispose();
        }
    }

    @Override
    public int getIconWidth() {
        return 18;
    }

    @Override
    public int getIconHeight() {
        return 18;
    }



}
