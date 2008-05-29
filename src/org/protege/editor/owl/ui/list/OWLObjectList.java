package org.protege.editor.owl.ui.list;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRendererSimple;
import org.protege.editor.owl.ui.transfer.OWLObjectListDragGestureListener;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;

import javax.swing.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLObjectList<O extends OWLObject> extends JList {

    public OWLObjectList(OWLEditorKit owlEditorKit) {
        setCellRenderer(new OWLCellRendererSimple(owlEditorKit));
        DragSource ds = DragSource.getDefaultDragSource();
        ds.createDefaultDragGestureRecognizer(this,
                                              DnDConstants.ACTION_COPY,
                                              new OWLObjectListDragGestureListener(owlEditorKit, this));
    }


    public String getToolTipText(MouseEvent event) {
        int index = locationToIndex(event.getPoint());
        if (index >= 0){
            Object element = getModel().getElementAt(index);
            if (element != null && element instanceof OWLEntity){
                return ((OWLEntity)element).getURI().toString();
            }
        }
        return null;
    }
}
