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
        Graphics2D g2 = (Graphics2D) g;
        Stroke stroke = g2.getStroke();
        Color color = g2.getColor();
        Object antiAliasing = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);

        entityIcon.paintIcon(c, g, x + 2, y + 1);
        
        int xC = getIconWidth() / 2 + x;
        int yC = getIconHeight() / 2 + y;
        g2.setStroke(EntityActionIcon.ACTION_STROKE);
        g.setColor(entityIcon.getEntityColor());
        int crossLegLen = 7;
        g.drawLine(xC - crossLegLen, yC - crossLegLen, xC + crossLegLen, yC + crossLegLen);
        g.drawLine(xC - crossLegLen, yC + crossLegLen, xC + crossLegLen, yC - crossLegLen);

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
