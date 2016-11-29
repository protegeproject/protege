package org.protege.editor.core.ui.view;

import javax.swing.*;
import java.awt.*;

import static java.awt.Color.WHITE;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 28 Nov 2016
 */
public class HorizontalMoreIcon implements Icon {

    public static final int DOT_SIZE = 4;

    private final int WIDTH = 18;

    private final int HEIGHT = 8;

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        g2.setColor(WHITE);
        g2.translate(0, HEIGHT / 2 - DOT_SIZE / 2);
        g2.fillOval(0, 0, DOT_SIZE, DOT_SIZE);
        g2.translate(5, 0);
        g2.fillOval(0, 0, DOT_SIZE, DOT_SIZE);
        g2.translate(5, 0);
        g2.fillOval(0, 0, DOT_SIZE, DOT_SIZE);
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
