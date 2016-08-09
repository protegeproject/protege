package org.protege.editor.owl.ui.renderer;

import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 9 Aug 16
 */
public class DeleteEntityIcon implements Icon {


    private static final BasicStroke DELETE_SYMBOL_STROKE = new BasicStroke(2);

    private final Icon entityIcon;

    private final Color entityColor;

    public DeleteEntityIcon(OWLEntityIcon entityIcon) {
        this.entityIcon = entityIcon;
        this.entityColor = entityIcon.getEntityColor();
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        Stroke stroke = g2.getStroke();
        Color color = g2.getColor();
        Object antiAliasing = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);

        Composite composite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));

        entityIcon.paintIcon(c, g, x + 2, y + 1);

        g2.setComposite(composite);

        int xC = getIconWidth() / 2 + x;
        int yC = getIconHeight() / 2 + y;
        g2.setStroke(DELETE_SYMBOL_STROKE);
        g.setColor(entityColor);
        g.drawLine(xC - 5, yC - 5, xC + 5, yC + 5);
        g.drawLine(xC - 5, yC + 5, xC + 5, yC - 5);

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
