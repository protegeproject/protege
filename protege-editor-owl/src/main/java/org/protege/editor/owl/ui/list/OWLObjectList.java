package org.protege.editor.owl.ui.list;

import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRendererSimple;
import org.protege.editor.owl.ui.transfer.OWLObjectListDragGestureListener;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLObjectList<O extends OWLObject> extends JList<O> {


    public OWLObjectList(OWLEditorKit owlEditorKit) {
        OWLCellRendererSimple renderer = new OWLCellRendererSimple(owlEditorKit);
        renderer.setDisplayQuotes(false);
        setCellRenderer(renderer);
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
                return ((OWLEntity)element).getIRI().toString();
            }
        }
        return null;
    }


    public void setSelectedValues(Set<O> owlObjects, boolean shouldScroll) {
        getSelectionModel().clearSelection();
        if (getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION){
            int firstIndex = -1;
            for (int i=0; i<getModel().getSize(); i++){
                if (owlObjects.contains(getModel().getElementAt(i))){
                    getSelectionModel().addSelectionInterval(i, i);
                    if (firstIndex == -1){
                        firstIndex = i;
                    }
                }
            }
            if (shouldScroll && firstIndex != -1){
                scrollRectToVisible(new Rectangle(getCellBounds(firstIndex, firstIndex)));
            }
        }
    }


    @SuppressWarnings("unchecked")
    public java.util.List<O> getSelectedOWLObjects(){
        List<O> sel = new ArrayList<>();
        for (Object o : getSelectedValues()){
            sel.add((O) o);
        }
        return sel;
    }
}
