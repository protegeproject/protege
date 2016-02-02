package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLIndividualSelectorPanel;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.swing.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLIndividualSetEditor extends AbstractOWLObjectEditor<Set<OWLNamedIndividual>>  implements VerifiedInputEditor  {

    private OWLIndividualSelectorPanel panel;

    private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();
    
    private InputVerificationStatusChangedListener inputListener = newState -> handleVerifyEditorContents();

    public OWLIndividualSetEditor(OWLEditorKit owlEditorKit) {
        panel = new OWLIndividualSelectorPanel(owlEditorKit, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        panel.addStatusChangedListener(inputListener);
    }


    public Set<OWLNamedIndividual> getEditedObject() {
        return panel.getSelectedObjects();
    }


    public boolean setEditedObject(Set<OWLNamedIndividual> individuals) {
        panel.setSelection(individuals != null ? individuals : Collections.EMPTY_SET);
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
    
    private void handleVerifyEditorContents() {
    	for (InputVerificationStatusChangedListener l : listeners){
    		l.verifiedStatusChanged(true);
    	}
    }
    
    public void addStatusChangedListener(InputVerificationStatusChangedListener l) {
        listeners.add(l);
        l.verifiedStatusChanged(true);
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener l) {
        listeners.remove(l);
    }


    public void dispose() {
        panel.dispose();
    }
}
