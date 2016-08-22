package org.protege.editor.owl.ui.util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Aug 16
 */
public class NothingSelectedPanel extends JPanel {

    public static final String NOTHING_SELECTED = "Nothing Selected";

    private static final Color BORDER_COLOR = new Color(220, 220, 220);

    private static final Color TEXT_COLOR = new Color(200, 200, 200);

    private static final int BORDER_MARGIN = 20;

    private static final int BORDER_THICKNESS = 8;

    private static final Font FONT = new Font("Sans-serif", Font.PLAIN, 40);

    public NothingSelectedPanel() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Stroke currentStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(BORDER_THICKNESS));
        g2.setColor(BORDER_COLOR);
        int borderWidth = getWidth() - 2 * BORDER_MARGIN;
        int borderHeight = getHeight() - 2 * BORDER_MARGIN;
        g2.drawRoundRect(BORDER_MARGIN, BORDER_MARGIN, borderWidth, borderHeight, BORDER_MARGIN, BORDER_MARGIN);
        g2.setStroke(currentStroke);
        g2.setColor(TEXT_COLOR);
        g2.setFont(FONT);
        Rectangle2D rectangle = g2.getFontMetrics().getStringBounds(NOTHING_SELECTED, g2);
        int stringX = (int) (getWidth() - rectangle.getWidth()) / 2;
        int stringY = (int) (getHeight() - rectangle.getHeight()) / 2;
        g2.drawString(NOTHING_SELECTED, stringX, stringY);
    }
}
