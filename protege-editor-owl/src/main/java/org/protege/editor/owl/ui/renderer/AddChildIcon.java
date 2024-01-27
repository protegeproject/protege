package org.protege.editor.owl.ui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.Icon;

import org.protege.editor.core.ui.renderer.HasUseSystemForeground;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 9 Aug 16
 */
public class AddChildIcon implements Icon, HasUseSystemForeground {

    private final OWLEntityIcon entityIcon;

    public AddChildIcon(OWLEntityIcon entityIcon) {
        this.entityIcon = entityIcon;
    }

    @Override
    public Icon useSystemForeground() {
        entityIcon.setOverriderColorToForegroundColor();
        return this;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();

        try {
            EntityActionIcon.setupState(c, g2, entityIcon);
            Color oldColor = g2.getColor();
            Stroke oldStroke = g2.getStroke();

            int iconWidth = 18;
            int iconHeight = 18;

            int parCX = x + iconWidth / 4;
            int parCY = y + iconHeight / 4;

            int childCX = x +  (iconWidth * 3) / 4;
            int childCY = y + (iconHeight * 3) / 4;

            g2.setStroke(EntityActionIcon.ACTION_STROKE);
            g2.setColor(entityIcon.getColor());

            int [] xPoints = {parCX, parCX, childCX};
            int [] yPoints = {parCY, childCY, childCY};
            g2.drawPolyline(xPoints, yPoints, 3);

            int addX = x + iconWidth - 4;
            int addY = y + 4;
            int addLegLen = 3;


            g2.drawLine(addX - addLegLen, addY, addX + addLegLen, addY);
            g2.drawLine(addX, addY - addLegLen, addX, addY + addLegLen);

            g2.setStroke(oldStroke);
            g2.setColor(oldColor);

            g2.translate(parCX, parCY);

            double parentChildScaleFactor = 0.6;
            g2.scale(parentChildScaleFactor, parentChildScaleFactor);
            int parX = -entityIcon.getBaseSize() / 2;
            int parY = -entityIcon.getBaseSize() / 2;
            entityIcon.paintIcon(c, g2, parX, parY);
            g2.scale(1 / parentChildScaleFactor, 1 / parentChildScaleFactor);
            g2.translate(-parCX, -parCY);

            g2.translate(childCX, childCY);
            int childX = -entityIcon.getBaseSize() / 2;
            int childY = -entityIcon.getBaseSize() / 2;
            g2.scale(parentChildScaleFactor, parentChildScaleFactor);
            entityIcon.paintIcon(c, g2, childX, childY);
            g2.scale(1 / parentChildScaleFactor, 1 / parentChildScaleFactor);
            g2.translate(-childCX, -childCY);
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
