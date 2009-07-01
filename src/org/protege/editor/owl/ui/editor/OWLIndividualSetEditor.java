package org.protege.editor.owl.ui.editor;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLIndividualSelectorPanel;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.swing.*;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLIndividualSetEditor extends AbstractOWLObjectEditor<Set<OWLNamedIndividual>> {

    private OWLIndividualSelectorPanel panel;


    public OWLIndividualSetEditor(OWLEditorKit owlEditorKit) {
        panel = new OWLIndividualSelectorPanel(owlEditorKit, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }


    public Set<OWLNamedIndividual> getEditedObject() {
        return panel.getSelectedObjects();
    }


    public boolean setEditedObject(Set<OWLNamedIndividual> individuals) {
        panel.setSelection(individuals);
        return true;
    }


    public String getEditorTypeName() {
        return "Set of named Individuals";
    }


    public boolean canEdit(Object object) {
        return checkSet(object, OWLNamedIndividual.class);
    }


    public JComponent getEditorComponent() {
        return panel;
    }


    public void dispose() {
        panel.dispose();
    }
}
