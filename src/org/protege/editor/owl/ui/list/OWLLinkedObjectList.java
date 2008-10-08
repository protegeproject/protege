package org.protege.editor.owl.ui.list;

import org.protege.editor.owl.ui.renderer.LinkedObjectComponent;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponentMediator;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLObject;

import javax.swing.*;
import java.awt.*;
/*
 * Copyright (C) 2008, University of Manchester
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
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 04-Oct-2008<br><br>
 */
public class OWLLinkedObjectList extends OWLObjectList implements LinkedObjectComponent {

    private LinkedObjectComponentMediator mediator;


    public OWLLinkedObjectList(OWLEditorKit owlEditorKit) {
        super(owlEditorKit);
        this.mediator = new LinkedObjectComponentMediator(owlEditorKit, this);
    }


    /**
         * Gets the location of the mouse relative to the rendering cell that it is
         * over.
         */
        public Point getMouseCellLocation() {
            Point mouseLoc = getMousePosition();
            if (mouseLoc == null) {
                return null;
            }
            int index = locationToIndex(mouseLoc);
            Rectangle cellRect = getCellBounds(index, index);
            return new Point(mouseLoc.x - cellRect.x, mouseLoc.y - cellRect.y);
        }

        public Rectangle getMouseCellRect() {
            Point loc = getMousePosition();
            if (loc == null) {
                return null;
            }
            int index = locationToIndex(loc);
            return getCellBounds(index, index);
        }
    

    //    public Object getCellObject();
    public void setLinkedObject(OWLObject object) {
        mediator.setLinkedObject(object);
    }


    public OWLObject getLinkedObject() {
        return mediator.getLinkedObject();
    }


    public JComponent getComponent() {
        return this;
    }
}
