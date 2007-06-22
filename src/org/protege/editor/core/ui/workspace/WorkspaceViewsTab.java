package org.protege.editor.core.ui.workspace;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.view.ViewsPane;
import org.protege.editor.core.ui.view.ViewsPaneMemento;

import javax.swing.*;
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
 * Date: Apr 6, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class WorkspaceViewsTab extends WorkspaceTab {

    public static final Logger logger = Logger.getLogger(WorkspaceViewsTab.class);

    private ViewsPane viewsPane;


    public ViewsPane getViewsPane() {
        return viewsPane;
    }


    public void initialise() {
        setLayout(new BorderLayout());
        ViewsPaneMemento memento = new ViewsPaneMemento(this);
        viewsPane = new ViewsPane(getWorkspace(), memento);
        add(viewsPane, BorderLayout.CENTER);
        getWorkspace().getViewManager().registerViews(this);
    }


    protected void setTopComponent(JComponent component) {
        add(component, BorderLayout.NORTH);
    }


    protected void setLeftComponent(JComponent component) {
        add(component, BorderLayout.WEST);
    }


    protected void setRightComponent(JComponent component) {
        add(component, BorderLayout.EAST);
    }


    protected void setBottomComponent(JComponent component) {
        add(component, BorderLayout.SOUTH);
    }


    public void bringViewToFront(String viewId) {
        viewsPane.bringViewToFront(viewId);
    }


    public void save() {
        super.save();
        viewsPane.saveViews();
    }


    public void dispose() {
        // Save the current views
        viewsPane.saveViews();
        // Dispose of the views
        logger.debug("Disposing of views");
        viewsPane.dispose();
    }
}
