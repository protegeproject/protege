package org.protege.editor.owl.ui.renderer;

import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 9 Aug 16
 */
public class AddEntityIcon implements Icon {

    private static final BasicStroke ADD_SYMBOL_STROKE = new BasicStroke(2);

    private final Icon entityIcon;

    private final Color entityColor;

    public AddEntityIcon(Icon entityIcon, Color entityColor) {
        this.entityIcon = entityIcon;
        this.entityColor = entityColor;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        Stroke stroke = g2.getStroke();
        Color color = g2.getColor();
        Object antiAliasing = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);


        entityIcon.paintIcon(c, g, x, y + 3);
        int xC = getIconWidth() + x - 5;
        int yC = y + 4;
        g2.setStroke(ADD_SYMBOL_STROKE);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(entityColor);
        g.drawLine(xC - 3, yC, xC + 3, yC);
        g.drawLine(xC, yC - 3, xC, yC + 3);

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
