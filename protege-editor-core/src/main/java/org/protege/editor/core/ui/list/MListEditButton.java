package org.protege.editor.core.ui.list;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Feb-2007<br><br>
 */
public class MListEditButton extends MListButton {

	private final Ellipse2D border_ = new Ellipse2D.Float();
	
    public MListEditButton(ActionListener actionListener) {
        super("Edit", new Color(20, 80, 210), actionListener);
    }

    @Override
	public void paintButtonContent(Graphics2D g) {
        Rectangle bounds = getBounds();
        int x = bounds.x;
        int y = bounds.y;
        int size = bounds.width;
        int quarterSize = (Math.round(bounds.width / 4.0f) / 2) * 2;
        border_.setFrame(x + size / 2 - quarterSize, y + size / 2 - quarterSize, 2 * quarterSize, 2 * quarterSize);
        Area area = new Area(border_);
        border_.setFrame(x + size / 2 - quarterSize / 2, y + size / 2 - quarterSize / 2, quarterSize, quarterSize);
        area.subtract(new Area(border_));
        g.fill(area);
    }

    @Override
    protected int getSizeMultiple() {
        return 4;
    }
}
