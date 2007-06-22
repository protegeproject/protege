package org.protege.editor.core.ui.split;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
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
 * Date: Mar 19, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ViewSplitPaneDivider extends BasicSplitPaneDivider {

    // Debugging can be turned on (requires recompile) to
    // highlight the divider position and size
    public static final boolean DEBUG = false;


    public ViewSplitPaneDivider(BasicSplitPaneUI ui) {
        super(ui);
        setLayout(new BorderLayout());
        add(new DividerComponent());
    }


    /**
     * Paints the divider.
     */
    public void paint(Graphics g) {
        // We just want to paint the child components - i.e.
        // the divider panel.  This ensures that look & feel
        // specific rendering, such as grips/bumps are not
        // shown.
        paintComponents(g);
    }


    private static class DividerComponent extends JPanel {

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (DEBUG) {
                Color oldColor = g.getColor();
                int width = getWidth();
                int height = getHeight();
                g.setColor(Color.MAGENTA);
                if (height > width) {
                    g.drawLine(width / 2, 2, width / 2, height - 2);
                }
                else {
                    g.drawLine(2, height / 2, width - 2, height / 2);
                }
                g.setColor(oldColor);
            }
        }
    }
}
