package org.protege.editor.core.ui.list;

import java.awt.*;
import java.awt.event.ActionListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Feb-2007<br><br>
 */
public class MListDeleteButton extends MListButton {

    public static final Color ROLL_OVER_COLOR = new Color(240, 40, 40);


    public MListDeleteButton(ActionListener actionListener) {
        super("Remove", ROLL_OVER_COLOR, actionListener);
    }


    public void paintButtonContent(Graphics2D g) {
        // A small cross oriented at 45 degrees
        int inset = 5;
        int x = getBounds().x;
        int y = getBounds().y;
        Dimension dim = getBounds().getSize();
        g.drawLine(x + inset, y + inset, x + dim.width - inset, y + dim.height - inset);
        g.drawLine(x + inset, y + dim.height - inset, x + dim.width - inset, y + inset);
    }
}
