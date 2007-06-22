package org.protege.editor.owl.ui.table;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponent;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponentMediator;
import org.semanticweb.owl.model.OWLObject;

import javax.swing.*;
import javax.swing.table.TableModel;
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
 * Date: 14-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class BasicLinkedOWLObjectTable extends BasicOWLTable implements LinkedObjectComponent {

    private OWLEditorKit editorKit;


    public BasicLinkedOWLObjectTable(TableModel model, OWLEditorKit owlEditorKit) {
        super(model);
        this.editorKit = owlEditorKit;
        defaultCursor = getCursor();
        LinkedObjectComponentMediator mediator = new LinkedObjectComponentMediator(owlEditorKit, this);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Linked object component stuff
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    public OWLEditorKit getOWLEditorKit() {
        return editorKit;
    }


    /**
     * Gets the location of the mouse relative to the
     * rendering cell that it is over.
     */
    public Point getMouseCellLocation() {
        Point mouseLoc = getMousePosition();
        if (mouseLoc == null) {
            return null;
        }
        int row = rowAtPoint(mouseLoc);
        int col = columnAtPoint(mouseLoc);
        Rectangle cellRect = getCellRect(row, col, true);
        return new Point(mouseLoc.x - cellRect.x, mouseLoc.y - cellRect.y);
    }


    public Rectangle getMouseCellRect() {
        Point mousePos = getMousePosition();
        if (mousePos != null) {
            return getCellRect(rowAtPoint(mousePos), columnAtPoint(mousePos), true);
        }
        else {
            return null;
        }
    }


    /**
     * Gets the cell object that the mouse is over
     */
    public Object getCellObject() {
        Point mouseLoc = getMousePosition();
        if (mouseLoc == null) {
            return null;
        }
        int row = rowAtPoint(mouseLoc);
        int col = columnAtPoint(mouseLoc);
        if (row > -1 && col > -1) {
            return getModel().getValueAt(row, col);
        }
        else {
            return null;
        }
    }


    private OWLObject linkedObject;

    private Cursor defaultCursor;


    public void setLinkedObject(OWLObject object) {
        linkedObject = object;
        if (linkedObject != null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        else {
            setCursor(defaultCursor);
        }
    }


    public OWLObject getLinkedObject() {
        return linkedObject;
    }


    public JComponent getComponent() {
        return this;
    }
}
