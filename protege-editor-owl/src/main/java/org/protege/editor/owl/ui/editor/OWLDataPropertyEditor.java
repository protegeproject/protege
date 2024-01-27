package org.protege.editor.owl.ui.editor;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JComponent;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLDataPropertySelectorPanel;
import org.semanticweb.owlapi.model.OWLDataProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDataPropertyEditor extends AbstractOWLObjectEditor<OWLDataProperty> implements VerifiedInputEditor {

    private OWLDataPropertySelectorPanel editor;

    private Set<InputVerificationStatusChangedListener> listeners = new HashSet<>();
    
    private InputVerificationStatusChangedListener inputListener = newState -> handleVerifyEditorContents();

    public OWLDataPropertyEditor(OWLEditorKit owlEditorKit) {
        editor = new OWLDataPropertySelectorPanel(owlEditorKit);
        editor.addStatusChangedListener(inputListener);
    }


    @Nullable
    public OWLDataProperty getEditedObject() {
        return editor.getSelectedObject();
    }

    public boolean setEditedObject(OWLDataProperty p){
        editor.setSelection(p);
        return true;
    }


    @Nonnull
    public String getEditorTypeName() {
        return "Data property";
    }


    public boolean canEdit(Object object) {
        return object instanceof OWLDataProperty;
    }


    @Nonnull
    public JComponent getEditorComponent() {
        return editor;
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
        editor.dispose();
    }
}
