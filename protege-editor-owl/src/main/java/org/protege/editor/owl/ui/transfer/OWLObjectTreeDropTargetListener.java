package org.protege.editor.owl.ui.transfer;

import org.protege.editor.owl.ui.table.OWLObjectDropTargetListener;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.protege.editor.owl.ui.tree.OWLTreePreferences;

import java.awt.*;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;


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

    private final OWLObjectTree tree;

    private final OWLTreePreferences treePreferences;

    public OWLObjectTreeDropTargetListener(OWLObjectTree component, OWLTreePreferences treePreferences) {
        super(component);
        this.tree = component;
        this.treePreferences = treePreferences;
    }


    protected boolean isDragAcceptable(DropTargetDragEvent event) {
        int row = tree.getRowForLocation(event.getLocation().x, event.getLocation().y);
        if (row == -1) {
            return false;
        }
        Rectangle r = tree.getRowBounds(row);
        if (!r.contains(event.getLocation())) {
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
        return treePreferences.isTreeDragAndDropEnabled() && super.isDropAcceptable(event);
    }


    public void drop(DropTargetDropEvent dtde) {
        super.drop(dtde);
        tree.setDropRow(-1);
    }
}
