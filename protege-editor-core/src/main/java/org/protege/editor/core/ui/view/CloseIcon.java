package org.protege.editor.core.ui.view;

import java.awt.BasicStroke;
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
public class CloseIcon extends ViewBarIcon {

    private static final BasicStroke STROKE = new BasicStroke(2f);

    private static final CloseIcon ICON = new CloseIcon();

    private CloseIcon() {
    }

    /**
     * Gets the Close View icon.
     * @return The Close View icon.
     */
    @Nonnull
    public static CloseIcon get() {
        return ICON;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.WHITE);
        int width = getIconWidth();
        int height = getIconHeight();
        g2.drawRect(1, 1, width - 2, height - 2);
        g2.setStroke(STROKE);
        g2.drawLine(4, 4, width - 4, height - 4);
        g2.drawLine(4, height - 4, width - 4, 4);
    }

}
