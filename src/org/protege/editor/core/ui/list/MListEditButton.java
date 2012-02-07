package org.protege.editor.core.ui.list;

import java.awt.*;
import java.awt.event.ActionListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Feb-2007<br><br>
 */
public class MListEditButton extends MListButton {

    public MListEditButton(ActionListener actionListener) {
        super("Edit", new Color(20, 80, 210), actionListener);
    }


    public void paintButtonContent(Graphics2D g) {
        Rectangle bounds = getBounds();
        int x = bounds.x;
        int y = bounds.y;
        int size = bounds.width;
        int quarterSize = (Math.round(bounds.width / 4.0f) / 2) * 2;
        g.fillOval(x + size / 2 - quarterSize, y + size / 2 - quarterSize, 2 * quarterSize, 2 * quarterSize);
        g.setColor(getBackground());
        g.fillOval(x + size / 2 - quarterSize / 2, y + size / 2 - quarterSize / 2, quarterSize, quarterSize);
    }

    @Override
    protected int getSizeMultiple() {
        return 4;
    }
}
