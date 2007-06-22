package org.protege.editor.core.ui.view;

import org.protege.editor.core.ui.split.ViewSplitPane;

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
 * Date: Apr 6, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class NestedViewSplitPane extends ViewSplitPane {


    private static final int ZERO_CONTENT_COUNT = 1;

    private JSplitPane parentComponent;

    private String locationInParent;


    public NestedViewSplitPane(JSplitPane parentSplitPane, String locationInParent, int orientation) {
        super(orientation);
        this.parentComponent = parentSplitPane;
        this.locationInParent = locationInParent;
        addContainerListener(new ContainerListener() {

            public void componentAdded(ContainerEvent e) {
                processComponentAdded();
            }


            public void componentRemoved(ContainerEvent e) {
                processComponentRemoved();
            }
        });
    }


    private void processComponentAdded() {
        if (getComponentCount() == ZERO_CONTENT_COUNT + 1) {
            if (locationInParent.equals(JSplitPane.LEFT) || locationInParent.equals(JSplitPane.TOP)) {
                parentComponent.setLeftComponent(this);
            }
            else {
                parentComponent.setBottomComponent(this);
            }
        }
    }


    private void processComponentRemoved() {
        if (getComponentCount() == ZERO_CONTENT_COUNT) {
            if (locationInParent.equals(JSplitPane.LEFT) || locationInParent.equals(JSplitPane.TOP)) {
                parentComponent.setTopComponent(null);
            }
            else {
                parentComponent.setBottomComponent(null);
            }
        }
    }
}
