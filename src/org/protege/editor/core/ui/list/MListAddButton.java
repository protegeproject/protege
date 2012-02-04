package org.protege.editor.core.ui.list;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Feb-2007<br><br>
 */
public class MListAddButton extends MListButton {

    public MListAddButton(ActionListener actionListener) {
        super("Add", Color.GREEN.darker(), actionListener);
    }


    public void paintButtonContent(Graphics2D g) {
        int inset = 5;
        Dimension dim = getBounds().getSize();
        int x = getBounds().x;
        int y = getBounds().y;
        int midLine = (int) Math.round(dim.width / 2.0);
        g.drawLine(x + midLine, y + inset, x + midLine, y + dim.height - inset);
        g.drawLine(x + inset, y + dim.height / 2, x + dim.width - inset, y + dim.height / 2);
    }
}
