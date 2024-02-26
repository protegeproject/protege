package org.protege.editor.core.ui.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.annotation.Nonnull;
import javax.swing.Icon;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-18
 */
public class SearchIcon implements Icon {

    private static final int WIDTH = 18;

    private static final int HEIGHT = 18;

    private final Color iconColor;

    public static final BasicStroke RIM_STROKE = new BasicStroke(2f);

    public static final BasicStroke HANDLE_STROKE = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    public SearchIcon(@Nonnull Color color) {
        this.iconColor = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(iconColor);
        g.translate(x, y);
        Graphics2D g2 = (Graphics2D) g;
        Stroke s = g2.getStroke();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setStroke(HANDLE_STROKE);
        g.drawLine(11, 11, 16, 16);

        g2.setStroke(RIM_STROKE);
        g.setColor(iconColor);
        g.drawOval(1, 1, 11, 11);
        g2.setStroke(s);
        g.translate(-x, -y);
        g2.scale(1, 1);
    }

    @Override
    public int getIconWidth() {
        return WIDTH;
    }

    @Override
    public int getIconHeight() {
        return HEIGHT;
    }
}
