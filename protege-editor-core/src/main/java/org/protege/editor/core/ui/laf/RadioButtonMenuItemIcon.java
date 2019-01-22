package org.protege.editor.core.ui.laf;

import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-01-22
 */
public class RadioButtonMenuItemIcon implements Icon {


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
        JRadioButtonMenuItem item = (JRadioButtonMenuItem) c;
        if(!item.isSelected()) {
            return;
        }
        int height = item.getHeight();
        Graphics2D g2 = (Graphics2D) g;
        int translate = (height - getIconHeight()) / 2;
        g2.translate(2, translate);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillOval(4, 3, 7, 7);
        g2.translate(-2, -translate);
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
