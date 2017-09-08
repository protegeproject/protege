package org.protege.editor.core.ui.menu;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.awt.BorderLayout.NORTH;
import static java.awt.Color.DARK_GRAY;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Sep 2017
 *
 * An icon that paints a 3 bar menu icon
 */
public class MenuIcon implements Icon {

    private final int WIDTH = 16;

    private final int HEIGHT = 16;

    @Nonnull
    private final Color color;


    public MenuIcon(@Nonnull Color color) {
        this.color = checkNotNull(color);
    }

    public static MenuIcon getGrayIcon() {
        return new MenuIcon(Color.GRAY);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        g2.setColor(color);

        final int padding = 1;
        final int barWidth = WIDTH - (2 * padding);
        final int barHeight = 3;
        final int spacingHeight = 2;

        int xOffset = x + 1;
        int yOffset = y + 2;

        g2.fillRect(xOffset, yOffset, barWidth, barHeight);
        yOffset += (barHeight + spacingHeight);
        g2.fillRect(xOffset, yOffset, barWidth, barHeight);
        yOffset += (barHeight + spacingHeight);
        g2.fillRect(xOffset, yOffset, barWidth, barHeight);
    }

    @Override
    public int getIconWidth() {
        return WIDTH;
    }

    @Override
    public int getIconHeight() {
        return HEIGHT;
    }

    public static void main(String[] args) {
        JLabel label = new JLabel("Hello");
        label.setIcon(MenuIcon.getGrayIcon());
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(label, NORTH);
        f.pack();
        f.setVisible(true);
    }
}
