package org.protege.editor.core.ui.util;

import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 4 Aug 16
 */
public class SearchIcon implements Icon {

    private static final int WIDTH = 12;

    private static final int HEIGHT = 12;

    private double scaleFactor = 1.0;

    public static final BasicStroke RIM_STROKE = new BasicStroke(2f);

    public static final BasicStroke HANDLE_STROKE = new BasicStroke(3f);

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Color color = g.getColor();
        g.translate(x, y);
        Graphics2D g2 = (Graphics2D) g;
        Stroke s = g2.getStroke();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        g2.setStroke(HANDLE_STROKE);
        g.drawLine(6, 6, 12, 12);


        g.setColor(c.getBackground());
        g2.setStroke(RIM_STROKE);
        g.fillOval(1, 1, 8, 8);
        g.setColor(color);
        g.drawOval(1, 1, 8, 8);
        g.setColor(color);
        g2.setStroke(s);
        g.translate(-x, -y);
    }

    @Override
    public int getIconWidth() {
        return (int) (WIDTH * scaleFactor);
    }

    @Override
    public int getIconHeight() {
        return (int) (HEIGHT * scaleFactor);
    }

    /**
     * Sets the scale factor that can be used to scale the icon up or down.  The default value is 1.0.
     * @param scaleFactor The scale factor.
     */
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
}
