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
        int size = getBounds().height;
        int thickness = (Math.round(size / 8.0f) / 2) * 2;
        
        int x = getBounds().x;
        int y = getBounds().y;

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
