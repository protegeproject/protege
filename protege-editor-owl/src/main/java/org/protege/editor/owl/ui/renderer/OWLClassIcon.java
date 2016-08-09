package org.protege.editor.owl.ui.renderer;

import java.awt.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/02/2012
 */
public class OWLClassIcon extends OWLEntityIcon {

    public static final Color COLOR = OWLSystemColors.getOWLClassColor();

    public static final BasicStroke HOLLOW_STROKE = new BasicStroke(2);

    private Type type;

    public enum Type {
        PRIMITIVE,
        DEFINED
    }

    public OWLClassIcon() {
        this(Type.PRIMITIVE, FillType.FILLED);
    }

    public OWLClassIcon(Type type) {
        this(type, FillType.FILLED);
    }

    public OWLClassIcon(Type type, FillType fillType) {
        super(SizeBias.EVEN, fillType);
        this.type = type;
    }

    @Override
    public Color getEntityColor() {
        return COLOR;
    }

    /**
     * Draw the icon at the specified location.  Icon implementations
     * may use the Component argument to get properties useful for
     * painting, e.g. the foreground or background color.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.translate(getPadding(), getPadding());

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color oldColor = g.getColor();
        Stroke oldStroke = g2.getStroke();

        int size = getSize() - 2 * getPadding();

        if(getFillType() == FillType.FILLED) {
            g.setColor(Color.LIGHT_GRAY);
            g.fillOval(x, y, size, size);
            g.setColor(COLOR);
            g.fillOval(x + 1, y + 1, size - 2, size - 2);
        }
        else {
            g2.setStroke(HOLLOW_STROKE);
            g.setColor(getEntityColor());
            g.drawOval(x + 1, y + 1, size - 2, size - 2);
        }



        if (type.equals(Type.DEFINED)) {
            if (getFillType() == FillType.FILLED) {
                g2.setColor(Color.WHITE);
            }
            else {
                g2.setColor(getEntityColor());
            }
            int centreSize = (int) Math.sqrt(size / 2 * size / 2);
            int boxWidth = (centreSize / 2) * 2;
            int boxHeight = (centreSize / 5) * 5;
            int boxX = (size - boxWidth) / 2;
            int boxY = (size - boxHeight) / 2;
            g2.fillRect(x + boxX, y + boxY, boxWidth, boxHeight);
            int stripeHeight = boxHeight / 5;
            g2.setColor(COLOR);
            g2.fillRect(x + boxX, y + boxY + stripeHeight, boxWidth, stripeHeight);
            g2.fillRect(x + boxX, y + boxY + stripeHeight * 3, boxWidth, stripeHeight);
        }

        g.setColor(oldColor);
        g2.setStroke(oldStroke);
        g.translate(-getPadding(), -getPadding());

    }
}
