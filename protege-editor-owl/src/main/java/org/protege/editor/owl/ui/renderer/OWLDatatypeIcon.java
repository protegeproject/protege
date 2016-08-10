package org.protege.editor.owl.ui.renderer;

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
        super(fillType);
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
        Graphics2D g2 = (Graphics2D) g.create();

        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = 14;

            if (getFillType() == FillType.FILLED) {
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillOval(x + 1, y + 1, size, size);
                g2.setColor(COLOR);
                g2.fillOval(x + 2, y + 2, size - 2, size - 2);
            }
            else {
                g2.setColor(COLOR);
                g2.setStroke(HOLLOW_STROKE);
                g2.drawOval(x + 2, y + 2, size - 2, size - 2);
            }
        } finally {
            g2.dispose();
        }

    }
}
