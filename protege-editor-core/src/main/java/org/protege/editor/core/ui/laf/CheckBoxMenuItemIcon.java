package org.protege.editor.core.ui.laf;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-01-22
 */
public class CheckBoxMenuItemIcon implements Icon {


    private static final Stroke stroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    /**
     * Draw the icon at the specified location.  Icon implementations
     * may use the Component argument to get properties useful for
     * painting, e.g. the foreground or background color.
     *
     * @param c
     * @param g
     * @param x
     * @param y
     */
    @Override
    public void paintIcon(Component c,
                          Graphics g,
                          int x,
                          int y) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem) c;
        int height = item.getHeight();
        if(!item.isSelected()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        int translate = (height - getIconHeight()) / 2;
        g2.translate(2, translate);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(stroke);
        g2.drawPolyline(
                new int [] {3, 5, 11},
                new int [] {6, 10, 1},
                3
        );
        g2.translate(-2, -translate);
        g2.setStroke(oldStroke);
    }

    /**
     * Returns the icon's width.
     *
     * @return an int specifying the fixed width of the icon.
     */
    @Override
    public int getIconWidth() {
        return 12;
    }

    /**
     * Returns the icon's height.
     *
     * @return an int specifying the fixed height of the icon.
     */
    @Override
    public int getIconHeight() {
        return 12;
    }
}
