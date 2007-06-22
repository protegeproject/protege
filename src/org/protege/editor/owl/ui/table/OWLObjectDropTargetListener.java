package org.protege.editor.owl.ui.table;

import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.transfer.OWLObjectDataFlavor;
import org.protege.editor.owl.ui.transfer.OWLObjectDropTarget;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.util.OWLObjectDuplicator;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;
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
 * Date: 04-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLObjectDropTargetListener implements DropTargetListener {

    private static final Logger logger = Logger.getLogger(OWLObjectDropTargetListener.class);

    private Cursor orginalCursor;

    private JComponent component;

    private OWLObjectDropTarget target;


    public OWLObjectDropTargetListener(OWLObjectDropTarget target) {
        this.component = target.getComponent();
        this.target = target;
    }


    public void dragEnter(DropTargetDragEvent dtde) {
        orginalCursor = component.getCursor();
    }


    public void dragOver(DropTargetDragEvent dtde) {
        // Update cursor
        if (isDragAcceptable(dtde)) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        }
        else {
            dtde.rejectDrag();
        }
    }


    public void dropActionChanged(DropTargetDragEvent dtde) {

    }


    public void dragExit(DropTargetEvent dte) {
        // Reset cursor
        component.setCursor(orginalCursor);
    }


    public void drop(DropTargetDropEvent dtde) {
        if (isDropAcceptable(dtde) == false) {
            dtde.rejectDrop();
        }
        else {
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            List<OWLObject> objs = getDropObjects(dtde.getTransferable());
            List<OWLObject> dupObjs = new ArrayList<OWLObject>();
            OWLObject dupObj = null;
            for (OWLObject obj : objs) {
                OWLObjectDuplicator duplicator = new OWLObjectDuplicator(target.getOWLModelManager().getOWLDataFactory());
                dupObj = duplicator.duplicateObject(obj);
                dupObjs.add(dupObj);
            }
            dtde.dropComplete(target.dropOWLObjects(dupObjs, dtde.getLocation(), dtde.getDropAction()));
        }
        component.setCursor(orginalCursor);
    }


    protected boolean isDragAcceptable(DropTargetDragEvent event) {
        return getDropObjects(event.getTransferable()) != null;
    }


    protected boolean isDropAcceptable(DropTargetDropEvent event) {
        return getDropObjects(event.getTransferable()) != null;
    }


    protected List<OWLObject> getDropObjects(Transferable transferable) {
        if (isAcceptableTransferable(transferable) == false) {
            return null;
        }
        try {
            List<OWLObject> objs = (List<OWLObject>) transferable.getTransferData(OWLObjectDataFlavor.OWL_OBJECT_DATA_FLAVOR);
            return objs;
        }
        catch (UnsupportedFlavorException e) {
            logger.error(e);
        }
        catch (IOException e) {
            logger.error(e);
        }
        return null;
    }


    protected boolean isAcceptableTransferable(Transferable transferable) {
        DataFlavor [] flavors = transferable.getTransferDataFlavors();
        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(OWLObjectDataFlavor.OWL_OBJECT_DATA_FLAVOR)) {
                return true;
            }
        }
        return false;
    }
}
