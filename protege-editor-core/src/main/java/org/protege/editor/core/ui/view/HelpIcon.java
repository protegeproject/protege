package org.protege.editor.core.ui.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 28 Nov 2016
 */
public class HelpIcon extends ViewBarIcon {


    private static final String HELP_STRING = "?";

    private static final HelpIcon ICON = new HelpIcon();

    private final Font font;

    public HelpIcon() {
        this.font = new Font("Verdana", Font.BOLD, 9);
    }

    public static HelpIcon get() {
        return ICON;
    }


    @Override
    public void paintIcon(Component c, Graphics graphics, int x, int y) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setColor(Color.WHITE);
        int width = getIconWidth();
        int height = getIconHeight();
        g2.drawRect(1, 1, width - 2, height - 2);

        g2.setFont(font);
        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(HELP_STRING, g2);

        float xPos = (float) ((width - bounds.getWidth()) / 2);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();
        float yPos = ((height + 1) / 2 - (ascent + descent) / 2 + ascent - 1);
        g2.drawString(HELP_STRING,
                     xPos,
                     yPos);
        g2.translate(-x, -y);
    }
}
