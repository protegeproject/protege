package org.protege.editor.owl.ui.navigation;

import org.protege.editor.owl.ui.renderer.OWLSystemColors;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-17
 */
public class NavIcon implements Icon {

    private static final int WIDTH = 18;

    private static final int HEIGHT = 18;

    private static final BasicStroke STROKE = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    public enum Direction {
        BACK,
        FORWARD
    }

    @Nonnull
    private final Direction direction;

    public NavIcon(@Nonnull Direction direction) {
        this.direction = checkNotNull(direction);
    }

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
        if(c.isEnabled()) {
            g.setColor(OWLSystemColors.getForegroundColor());
        }
        else {
            g.setColor(Color.LIGHT_GRAY);
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(STROKE);
        g2.translate(x, y);
        int rightOffset = direction == Direction.BACK ? 3 : -3;
        int leftOffset = direction == Direction.BACK ? -4 : 4;
        int xCentre = getIconWidth() / 2;
        int [] xPoints = new int[]{xCentre + rightOffset, xCentre + leftOffset, xCentre + rightOffset};
        int [] yPoints = new int[]{3, getIconHeight() / 2, getIconHeight() - 3};
        g2.drawPolyline(xPoints, yPoints, 3);
        g2.translate(-x, -y);
    }

    /**
     * Returns the icon's width.
     *
     * @return an int specifying the fixed width of the icon.
     */
    @Override
    public int getIconWidth() {
        return WIDTH;
    }

    /**
     * Returns the icon's height.
     *
     * @return an int specifying the fixed height of the icon.
     */
    @Override
    public int getIconHeight() {
        return HEIGHT;
    }
}
