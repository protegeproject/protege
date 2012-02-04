package org.protege.editor.owl.ui.framelist;

import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;

import java.awt.*;
import java.awt.event.ActionListener;
/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 1, 2008<br><br>
 */
public class AxiomAnnotationButton extends MListButton {

    public static final Color ROLL_OVER_COLOR = new Color(0, 0, 0);

    private static final String ANNOTATE_STRING = "@";

    private boolean annotationPresent = false;


    public AxiomAnnotationButton(ActionListener actionListener) {
        super("Annotations", ROLL_OVER_COLOR, actionListener);
    }


    public void paintButtonContent(Graphics2D g) {

        int w = getBounds().width;
        int h = getBounds().height;
        int x = getBounds().x;
        int y = getBounds().y;

        Font font = g.getFont().deriveFont(Font.BOLD, OWLRendererPreferences.getInstance().getFontSize());
        g.setFont(font);
        FontMetrics fontMetrics = g.getFontMetrics(font);
        final Rectangle stringBounds = fontMetrics.getStringBounds(ANNOTATE_STRING, g).getBounds();
        int baseline = fontMetrics.getLeading() + fontMetrics.getAscent();
        g.drawString(ANNOTATE_STRING, x + w / 2 - stringBounds.width / 2, y + (h - stringBounds.height) / 2 + baseline );

        if (annotationPresent) {
            g.drawOval(x + 2, y + 2, w - 4, h - 4);
        }

        g.setFont(font);
    }


    public void setAnnotationPresent(boolean annotationPresent) {
        this.annotationPresent = annotationPresent;
    }
}
