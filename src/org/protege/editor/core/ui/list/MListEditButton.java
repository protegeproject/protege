package org.protege.editor.core.ui.list;

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
