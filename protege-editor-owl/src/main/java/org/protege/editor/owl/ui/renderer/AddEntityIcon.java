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
        Graphics2D g2 = (Graphics2D) g;
        EntityActionIcon.setupAlpha(c, g2);

        Stroke stroke = g2.getStroke();
        Color color = g2.getColor();
        Object antiAliasing = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);


        entityIcon.paintIcon(c, g, x, y);
        int addCrossLegLength = 2;
        int xC = x + entityIcon.getIconWidth() + addCrossLegLength;
        int yC = y + 4;
        g2.setStroke(EntityActionIcon.ACTION_STROKE);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(entityIcon.getEntityColor());
        g.drawLine(xC - addCrossLegLength, yC, xC + addCrossLegLength, yC);
        g.drawLine(xC, yC - addCrossLegLength, xC, yC + addCrossLegLength);

        g2.setStroke(stroke);
        g2.setColor(color);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAliasing);
    }

    @Override
    public int getIconWidth() {
        return entityIcon.getIconWidth() + 4;
    }

    @Override
    public int getIconHeight() {
        return entityIcon.getIconHeight() + 2;
    }
}
