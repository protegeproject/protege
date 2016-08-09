package org.protege.editor.owl.ui.renderer;

import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 9 Aug 16
 */
public class OWLDatatypeIcon extends OWLEntityIcon {


    public static final Color COLOR = OWLSystemColors.getOWLDatatypeColor();

    private static final BasicStroke HOLLOW_STROKE = new BasicStroke(2);

    public OWLDatatypeIcon() {
        this(FillType.FILLED);
    }

    public OWLDatatypeIcon(FillType fillType) {
        super(SizeBias.EVEN, fillType);
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

        if (getFillType() == FillType.FILLED) {
            g.setColor(Color.LIGHT_GRAY);
            g.fillOval(x, y, size, size);
            g.setColor(COLOR);
            g.fillOval(x + 1, y + 1, size - 2, size - 2);
        }
        else {
            g.setColor(COLOR);
            g2.setStroke(HOLLOW_STROKE);
            g.drawOval(x + 1, y + 1, size - 2, size - 2);
        }

        g2.setStroke(oldStroke);
        g.setColor(oldColor);
        g.translate(-getPadding(), -getPadding());

    }
}
