package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;

import java.awt.*;
import java.awt.event.*;
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
 * Date: 03-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LinkedObjectComponentMediator {

    private OWLEditorKit editorKit;

    private LinkedObjectComponent linkedObjectComponent;

    private MouseMotionListener mouseMotionListener;

    private MouseListener mouseListener;

    private Rectangle rect;

    private OWLObject linkedObject;

    private Cursor defaultCursor;


    public LinkedObjectComponentMediator(OWLEditorKit owlEditorKit, LinkedObjectComponent linkedObjectComponent) {
        this.linkedObjectComponent = linkedObjectComponent;
        this.editorKit = owlEditorKit;

        mouseMotionListener = new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                doUpdate(e.getPoint());
            }
        };
        mouseListener = new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                navigateToObject();
            }


            public void mouseExited(MouseEvent e) {
                doUpdate(e.getPoint());
            }
        };
        linkedObjectComponent.getComponent().addMouseMotionListener(mouseMotionListener);
        linkedObjectComponent.getComponent().addMouseListener(mouseListener);
    }


    public void setLinkedObject(OWLObject linkedObject) {
        this.linkedObject = linkedObject;
        if (linkedObject != null) {
            linkedObjectComponent.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        else {
            linkedObjectComponent.getComponent().setCursor(defaultCursor);
        }
    }


    private void navigateToObject() {
        OWLObject object = linkedObjectComponent.getLinkedObject();
        if (!(object instanceof OWLEntity)) {
            return;
        }
        editorKit.getOWLWorkspace().getOWLSelectionModel().setSelectedEntity((OWLEntity) object);
        editorKit.getOWLWorkspace().displayOWLEntity((OWLEntity) object);
    }


    private void doUpdate(Point mousePoint) {
        // Repaint the last rectangle
        if (rect != null) {
            linkedObjectComponent.getComponent().repaint(rect);
        }
        // Store the cell rect as the last cell rect
        rect = linkedObjectComponent.getMouseCellRect();
        if (rect != null) {
            linkedObjectComponent.getComponent().repaint(rect);
        }
    }


    public void dispose() {
        linkedObjectComponent.getComponent().removeMouseMotionListener(mouseMotionListener);
        linkedObjectComponent.getComponent().removeMouseListener(mouseListener);
    }


    public OWLObject getLinkedObject() {
        return linkedObject;
    }
}
