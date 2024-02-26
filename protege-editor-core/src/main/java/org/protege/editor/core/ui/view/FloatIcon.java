package org.protege.editor.core.ui.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 28 Nov 2016
 */
public class FloatIcon extends ViewBarIcon {

    public static final FloatIcon ICON = new FloatIcon();

    private FloatIcon() {
    }

    /**
     * Gets the Float View Icon.
     * @return The Float View Icon.
     */
    @Nonnull
    public static FloatIcon get() {
        return ICON;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.WHITE);
        int width = getIconWidth();
        int height = getIconHeight();
        g2.drawRect(1, 1, width - 2, height - 2);
        g2.fillRect(3, 3, width - 6, height - 6);
    }
}
