package org.protege.editor.owl.ui.renderer;

import org.protege.editor.core.ui.renderer.HasUseSystemForeground;

import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 9 Aug 16
 */
public class AddEntityIcon implements Icon, HasUseSystemForeground {

    private final OWLEntityIcon entityIcon;

    public AddEntityIcon(OWLEntityIcon entityIcon) {
        this.entityIcon = entityIcon;
    }

    @Override
    public AddEntityIcon useSystemForeground() {
        entityIcon.setOverriderColorToForegroundColor();
        return this;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            EntityActionIcon.setupState(c, g2, entityIcon);

            entityIcon.paintIcon(c, g2, x + 1, y + 1);
            int addCrossLegLength = 3;
            int xC = x + entityIcon.getIconWidth() + addCrossLegLength;
            int yC = y + 4;
            g2.setStroke(EntityActionIcon.ACTION_STROKE);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setColor(entityIcon.getColor());
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
