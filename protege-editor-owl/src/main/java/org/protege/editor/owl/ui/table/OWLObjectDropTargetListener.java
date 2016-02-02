package org.protege.editor.owl.ui.table;

import org.protege.editor.owl.ui.transfer.OWLObjectDataFlavor;
import org.protege.editor.owl.ui.transfer.OWLObjectDropTarget;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.util.OWLObjectDuplicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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

    private final Logger logger = LoggerFactory.getLogger(OWLObjectDropTargetListener.class);

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
        if (!isDropAcceptable(dtde)) {
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
        if (!isAcceptableTransferable(transferable)) {
            return null;
        }
        try {
            List<OWLObject> objs = (List<OWLObject>) transferable.getTransferData(OWLObjectDataFlavor.OWL_OBJECT_DATA_FLAVOR);
            return objs;
        }
        catch (UnsupportedFlavorException e) {
            logger.error("The type of object being dropped is not supported at the drop location.", e);
        }
        catch (IOException e) {
            logger.error("An error occurred whilst deserializing a dropped object.", e);
        }
        return Collections.emptyList();
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
