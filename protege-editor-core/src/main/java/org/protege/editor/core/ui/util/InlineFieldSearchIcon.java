package org.protege.editor.core.ui.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.Icon;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 4 Aug 16
 */
public class InlineFieldSearchIcon implements Icon {

    private static final int WIDTH = 12;

    private static final int HEIGHT = 12;

    private final Color iconColor;

    private double scaleFactor = 1.0;

    public static final BasicStroke RIM_STROKE = new BasicStroke(2f);

    public static final BasicStroke HANDLE_STROKE = new BasicStroke(3f);

    public InlineFieldSearchIcon(Color color) {
        this.iconColor = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(iconColor);
        g.translate(x, y);
        Graphics2D g2 = (Graphics2D) g;
        Stroke s = g2.getStroke();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.scale(scaleFactor, scaleFactor);

        g2.setStroke(HANDLE_STROKE);
        g.drawLine(6, 6, 11, 11);


        g.setColor(c.getBackground());
        g2.setStroke(RIM_STROKE);
        g.fillOval(1, 1, 8, 8);
        g.setColor(iconColor);
        g.drawOval(1, 1, 8, 8);
        g.setColor(iconColor);
        g2.setStroke(s);
        g.translate(-x, -y);
        g2.scale(1, 1);
    }

    @Override
    public int getIconWidth() {
        return (int) (WIDTH * scaleFactor + 0.5);
    }

    @Override
    public int getIconHeight() {
        return (int) (HEIGHT * scaleFactor + 0.5);
    }

    /**
     * Sets the scale factor that can be used to scale the icon up or down.  The default value is 1.0.
     * @param scaleFactor The scale factor.
     */
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
}
