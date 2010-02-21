package org.protege.editor.owl.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 08-Feb-2007<br><br>
 */
public class SelectedOWLEntityList extends JList implements OWLSelectionModelListener {

    /**
     * 
     */
    private static final long serialVersionUID = 5544177770304817471L;

    private OWLEditorKit owlEditorKit;

    private List<OWLEntity> selectionList;


    /**
     * Constructs a <code>JList</code> with an empty model.
     */
    public SelectedOWLEntityList(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        selectionList = new ArrayList<OWLEntity>();
        owlEditorKit.getWorkspace().getOWLSelectionModel().addListener(this);
    }


    public void dispose() {
        owlEditorKit.getWorkspace().getOWLSelectionModel().removeListener(this);
    }


    public void selectionChanged() {
        selectionList.add(0, owlEditorKit.getWorkspace().getOWLSelectionModel().getSelectedEntity());
        if (selectionList.size() > 10) {
            selectionList.remove(selectionList.size() - 1);
        }
        setListData(selectionList.toArray());
    }
}
