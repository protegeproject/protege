package org.protege.editor.owl.ui.transfer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 04-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLObjectListDragGestureListener extends OWLObjectDragGestureListener {

    private OWLObjectList owlObjectList;


    public OWLObjectListDragGestureListener(OWLEditorKit owlEditorKit, OWLObjectList owlObjectList) {
        super(owlEditorKit, owlObjectList);
        this.owlObjectList = owlObjectList;
    }


    protected Point getImageOffset() {
        return new Point(0, 0);
    }


    protected JComponent getRendererComponent() {
        return (JComponent) owlObjectList.getCellRenderer().getListCellRendererComponent(owlObjectList,
                                                                                         owlObjectList.getSelectedValue(),
                                                                                         owlObjectList.getSelectedIndex(),
                                                                                         true,
                                                                                         true);
    }


    protected Dimension getRendererComponentSize() {
        return getRendererComponent().getPreferredSize();
    }


    protected List<OWLObject> getSelectedObjects() {
        List<OWLObject> selObjects = new ArrayList<OWLObject>();
        for (Object o : owlObjectList.getSelectedValues()) {
            if (o instanceof OWLObject) {
                selObjects.add((OWLObject) o);
            }
        }
        return selObjects;
    }
}
