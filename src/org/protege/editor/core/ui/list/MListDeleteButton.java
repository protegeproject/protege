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


    public void paintButtonContent(Graphics2D gIn) {
        Graphics2D g = (Graphics2D) gIn.create();
        int size = getBounds().height;
        int thickness = (Math.round(size / 8.0f) / 2) * 2;

        int x = getBounds().x;
        int y = getBounds().y;

        g.rotate(Math.PI / 4, x + size / 2, y + size / 2);
        
        int insetX = size / 4;
        int insetY = size / 4;
        int insetHeight = size / 2;
        int insetWidth = size / 2;
        g.fillRect(x + size / 2  - thickness / 2, y + insetY, thickness, insetHeight);
        g.fillRect(x + insetX, y + size / 2 - thickness / 2, insetWidth, thickness);
    }

    @Override
    protected int getSizeMultiple() {
        return 4;
    }
}

