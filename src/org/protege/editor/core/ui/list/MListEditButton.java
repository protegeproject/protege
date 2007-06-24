package org.protege.editor.core.ui.list;

import java.awt.Color;
import java.awt.Graphics2D;
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
        int w = getBounds().width;
        int h = getBounds().height;
        int x = getBounds().x;
        int y = getBounds().y;
        g.drawOval(x + 6, y + 6, w - 12, h - 12);
    }
}
