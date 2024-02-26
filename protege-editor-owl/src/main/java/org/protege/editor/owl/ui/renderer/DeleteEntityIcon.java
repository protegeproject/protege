package org.protege.editor.owl.ui.renderer;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

import org.protege.editor.core.ui.renderer.HasUseSystemForeground;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 9 Aug 16
 */
public class DeleteEntityIcon implements Icon, HasUseSystemForeground {


    private final OWLEntityIcon entityIcon;

    public DeleteEntityIcon(OWLEntityIcon entityIcon) {
        this.entityIcon = entityIcon;
    }

    @Override
    public DeleteEntityIcon useSystemForeground() {
        entityIcon.setOverriderColorToForegroundColor();
        return this;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        EntityActionIcon.setupState(c, g2, entityIcon);
        try {
            int halfWidth = getIconWidth() / 2;
            int xC = halfWidth + x;
            int halfHeight = getIconHeight() / 2;
            int yC = halfHeight + y;
            entityIcon.paintIcon(c,
                                 g2,
                                 xC - (entityIcon.getIconWidth() / 2),
                                 yC - (entityIcon.getIconHeight() / 2));
            g2.setStroke(EntityActionIcon.ACTION_STROKE);
            g2.setColor(entityIcon.getColor());
            int crossLegLen = 7;
            g2.drawLine(xC - crossLegLen, yC - crossLegLen, xC + crossLegLen, yC + crossLegLen);
            g2.drawLine(xC - crossLegLen, yC + crossLegLen, xC + crossLegLen, yC - crossLegLen);

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
