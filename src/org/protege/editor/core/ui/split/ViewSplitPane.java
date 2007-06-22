package org.protege.editor.core.ui.split;

import javax.swing.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
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
 * <p/>
 * This is a customisation of a <code>JSplitPane</code>
 * that modifies the split pane dividers and borders.
 * <p/>
 * The split pane UI is replaced so that the divider
 * and borders are "flat".  The split pane also changes
 * the size of the splitter depending on the number of
 * components contained in the split pane.  If there is
 * only one component then the splitter is set to be a
 * zero width.
 */
public class ViewSplitPane extends JSplitPane {

    private static final int DEFAULT_DIVIDER_SIZE = 6;


    public ViewSplitPane(int orientation) {
        super(orientation);
        setUI(new ViewSplitPaneUI());
        setBorder(null);
        addContainerListener(new ContainerListener() {

            public void componentAdded(ContainerEvent e) {
                updateDividerSize();
            }


            public void componentRemoved(ContainerEvent e) {
                updateDividerSize();
            }
        });
        updateDividerSize();
    }


    public void updateUI() {
//        super.updateUI();
    }


    public int getMinimumDividerLocation() {
        return 0;
    }


    public int getMaximumDividerLocation() {
        return Integer.MAX_VALUE;
    }


    private void updateDividerSize() {
        if (getComponentCount() > 2) {
            setDividerSize(DEFAULT_DIVIDER_SIZE);
        }
        else {
            setDividerSize(0);
        }
    }
}
