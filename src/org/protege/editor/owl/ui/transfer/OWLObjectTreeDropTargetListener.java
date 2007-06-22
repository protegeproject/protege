package org.protege.editor.owl.ui.transfer;

import org.protege.editor.owl.ui.table.OWLObjectDropTargetListener;
import org.protege.editor.owl.ui.tree.OWLObjectTree;

import java.awt.*;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
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
 * Date: 04-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLObjectTreeDropTargetListener extends OWLObjectDropTargetListener {

    private OWLObjectTree tree;


    public OWLObjectTreeDropTargetListener(OWLObjectTree component) {
        super(component);
        this.tree = component;
    }


    protected boolean isDragAcceptable(DropTargetDragEvent event) {
        int row = tree.getRowForLocation(event.getLocation().x, event.getLocation().y);
        if (row == -1) {
            return false;
        }
        Rectangle r = tree.getRowBounds(row);
        if (r.contains(event.getLocation()) == false) {
            tree.setDropRow(-1);
            return false;
        }
        boolean isAcceptable = super.isDragAcceptable(event);
        if (isAcceptable) {
            tree.setDropRow(row);
        }
        return isAcceptable;
    }


    public void dragExit(DropTargetEvent dte) {
        super.dragExit(dte);
        tree.setDropRow(-1);
    }


    protected boolean isDropAcceptable(DropTargetDropEvent event) {
        return super.isDropAcceptable(event);
    }


    public void drop(DropTargetDropEvent dtde) {
        super.drop(dtde);
        tree.setDropRow(-1);
    }
}
