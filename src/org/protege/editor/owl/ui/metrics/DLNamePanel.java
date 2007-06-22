package org.protege.editor.owl.ui.metrics;

import org.semanticweb.owl.util.DLExpressivityChecker;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
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
 * Medical Informatics Group<br>
 * Date: 29-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DLNamePanel extends JPanel {

    private List<Icon> icons;

    private int maxHeight = 0;

    private int maxWidth = 0;


    private static final int TRACKING_ADJUSTMENT = -7;


    public DLNamePanel() {
        icons = new ArrayList<Icon>();
    }


    public void setConstructs(List<DLExpressivityChecker.Construct> constructs) {
        icons.clear();
        for (DLExpressivityChecker.Construct constuct : constructs) {
            Icon curIcon = ExpressivityIcons.getIcon(constuct);
            if (curIcon != null) {
                icons.add(curIcon);
                if (curIcon.getIconHeight() > maxHeight) {
                    maxHeight = curIcon.getIconHeight();
                }
                maxWidth += curIcon.getIconWidth() + TRACKING_ADJUSTMENT;
            }
        }
        repaint(0, 0, getWidth(), getHeight());
    }


    public Dimension getPreferredSize() {
        Insets insets = getInsets();
        return new Dimension(maxWidth + insets.left + insets.right, maxHeight + insets.top + insets.bottom);
    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Insets insets = getInsets();
        g.translate(insets.left, insets.top);
        Graphics2D g2 = (Graphics2D) g;
        int x = 0;
        for (Icon icon : icons) {
            int y = maxHeight - icon.getIconHeight();
            icon.paintIcon(this, g2, x, y);
            x += icon.getIconWidth() + TRACKING_ADJUSTMENT;
        }

        g.translate(-insets.left, -insets.top);
    }
}
