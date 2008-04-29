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
public class MListRemoveTypeButton extends MListButton {

    public static final Color ROLL_OVER_COLOR = new Color(240, 40, 40);


    public MListRemoveTypeButton(ActionListener actionListener) {
        super("Remove Type", ROLL_OVER_COLOR, actionListener);
    }


    public void paintButtonContent(Graphics2D g) {
        // A cap T with a line through it
        int inset = 5;
        int inset2 = 1;
        int x = getBounds().x;
        int y = getBounds().y;
        Dimension dim = getBounds().getSize();
        int midx = x + (dim.width / 2);
        int midy = y + (dim.height / 2);
            // vertical up the middle
        g.drawLine(midx, y+inset, midx, y + dim.height - inset);
            // horizontal across the top
        g.drawLine(x + inset, y + inset, x + dim.width - inset, y + inset);
            // diagonal stike
        g.drawLine(x + inset, midy + 1, x + dim.width - inset, midy - 1);
    }
}
