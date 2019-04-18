package org.protege.editor.owl.ui.renderer;



import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-05
 *
 * Paints a cross hair icon in a circle
 */
public class ScrollToEntityIcon implements Icon {

    private static final int BASE_SIZE = 18;

    private static final BasicStroke STROKE = new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    @Override
    public void paintIcon(Component c,
                          Graphics g,
                          int x,
                          int y) {
        g.translate(x, y);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(STROKE);
        g2.setColor(OWLSystemColors.getForegroundColor());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawOval(3, 3, BASE_SIZE - 6, BASE_SIZE - 6);

        int outer = 4;
        int inner = 6;
        g2.drawLine(outer, BASE_SIZE / 2, inner, BASE_SIZE / 2);
        g2.drawLine(BASE_SIZE - outer, BASE_SIZE / 2, BASE_SIZE - inner, BASE_SIZE / 2);

        g2.drawLine(BASE_SIZE / 2, outer, BASE_SIZE / 2, inner);
        g2.drawLine(BASE_SIZE / 2, BASE_SIZE - outer, BASE_SIZE / 2, BASE_SIZE - inner);


        g.translate(-x, -y);
    }

    @Override
    public int getIconWidth() {
        return BASE_SIZE;
    }

    @Override
    public int getIconHeight() {
        return BASE_SIZE;
    }
}
