package org.protege.editor.owl.ui.renderer;

import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 9 Aug 16
 */
public class AddSiblingIcon implements Icon {



    private final OWLEntityIcon entityIcon;

    public AddSiblingIcon(OWLEntityIcon entityIcon) {
        this.entityIcon = entityIcon;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {

        Graphics2D g2 = (Graphics2D) g;

        EntityActionIcon.setupAlpha(c, g2);

        Color oldColor = g.getColor();
        Stroke oldStroke = g2.getStroke();

        int cx0 = x + getIconWidth() / 2 - 2;
        int cy0 = y + getIconHeight() / 4;

        int cx1 = x + getIconWidth() / 2 - 2;
        int cy1 = y + getIconHeight() * 3 / 4;

        g.setColor(entityIcon.getEntityColor());

        int backLegX = x + getIconWidth() / 4 - 3;
        g.drawLine(cx0, cy0, backLegX, cy0);
        g.drawLine(cx1, cy1, backLegX, cy1);
        g.drawLine(backLegX, cy0, backLegX, cy1);

        int addX = x + getIconWidth() - 3;
        int addY = cy1;
        int addLegLen = 2;

        g2.setStroke(EntityActionIcon.ACTION_STROKE);
        g.drawLine(addX - addLegLen, addY, addX + addLegLen, addY);
        g.drawLine(addX, addY - addLegLen, addX, addY + addLegLen);

        g2.setStroke(oldStroke);
        g.setColor(oldColor);

        double scaleFactor = 0.7;
        g.translate(cx0, cy0);
        int topSibX = -entityIcon.getIconWidth() / 2;
        int topSibY = -entityIcon.getIconHeight() / 2;

        g2.scale(scaleFactor, scaleFactor);
        entityIcon.paintIcon(c, g, topSibX, topSibY);
        g2.scale(1 / scaleFactor, 1 / scaleFactor);
        g.translate(-cx0, -cy0);

        g.translate(cx1, cy1);
        int botSibX = -entityIcon.getIconWidth() / 2;
        int botSibY = -entityIcon.getIconHeight() / 2;
        g2.scale(scaleFactor, scaleFactor);
        entityIcon.paintIcon(c, g, botSibX, botSibY);
        g2.scale(1 / scaleFactor, 1 / scaleFactor);
        g.translate(-cx1, -cy1);

    }

    @Override
    public int getIconWidth() {
        return entityIcon.getIconWidth() + 2;
    }

    @Override
    public int getIconHeight() {
        return entityIcon.getIconHeight() + 2;
    }
}
