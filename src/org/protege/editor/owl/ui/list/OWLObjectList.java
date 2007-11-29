package org.protege.editor.owl.ui.list;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;

import javax.swing.JList;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.transfer.OWLObjectListDragGestureListener;
import org.protege.editor.owl.ui.renderer.OWLCellRendererSimple;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLObjectList extends JList {

    public OWLObjectList(OWLEditorKit owlEditorKit) {
        setCellRenderer(new OWLCellRendererSimple(owlEditorKit));
        DragSource ds = DragSource.getDefaultDragSource();
        ds.createDefaultDragGestureRecognizer(this,
                                              DnDConstants.ACTION_COPY,
                                              new OWLObjectListDragGestureListener(owlEditorKit, this));
    }
}
