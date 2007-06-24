package org.protege.editor.owl.model;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Mar-2007<br><br>
 */
public class OWLIndividualListModel implements ListModel {

    private OWLModelManager owlModelManager;


    public OWLIndividualListModel(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }


    public void dispose() {

    }


    public int getSize() {
        return 0;
    }


    public Object getElementAt(int index) {
        return null;
    }


    public void addListDataListener(ListDataListener l) {
    }


    public void removeListDataListener(ListDataListener l) {
    }
}
