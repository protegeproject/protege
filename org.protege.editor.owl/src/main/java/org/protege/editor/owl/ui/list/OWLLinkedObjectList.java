package org.protege.editor.owl.ui.list;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponent;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponentMediator;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.*;
import java.awt.*;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
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
