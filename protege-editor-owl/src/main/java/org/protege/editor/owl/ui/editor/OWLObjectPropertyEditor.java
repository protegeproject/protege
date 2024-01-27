package org.protege.editor.owl.ui.editor;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JComponent;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.semanticweb.owlapi.model.OWLObjectProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLObjectPropertyEditor extends AbstractOWLObjectEditor<OWLObjectProperty> implements VerifiedInputEditor {

    private OWLObjectPropertySelectorPanel editor;

    private Set<InputVerificationStatusChangedListener> listeners = new HashSet<>();
    
    private InputVerificationStatusChangedListener inputListener = newState -> handleVerifyEditorContents();

    public OWLObjectPropertyEditor(OWLEditorKit owlEditorKit) {
        editor = new OWLObjectPropertySelectorPanel(owlEditorKit);
        editor.addStatusChangedListener(inputListener);
    }


    @Nullable
    public OWLObjectProperty getEditedObject() {
        return editor.getSelectedObject();
    }

    public boolean setEditedObject(OWLObjectProperty p){
        editor.setSelection(p);
        return true;
    }


    @Nonnull
    public String getEditorTypeName() {
        return "Object property";
    }


    public boolean canEdit(Object object) {
        return object instanceof OWLObjectProperty;
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
