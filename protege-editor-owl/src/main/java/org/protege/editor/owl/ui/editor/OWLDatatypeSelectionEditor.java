package org.protege.editor.owl.ui.editor;

import javax.swing.JComponent;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLDataTypeSelectorPanel;
import org.semanticweb.owlapi.model.OWLDatatype;

public class OWLDatatypeSelectionEditor extends AbstractOWLObjectEditor<OWLDatatype>{
    
    private OWLDataTypeSelectorPanel datatypeSelector;
    
    public OWLDatatypeSelectionEditor(OWLEditorKit editorKit) {
        datatypeSelector = new OWLDataTypeSelectorPanel(editorKit);
    }

    public String getEditorTypeName() {
        return "OWL Datatype Selection Editor";
    }

    public boolean canEdit(Object object) {
        return object instanceof OWLDatatype;
    }

    public JComponent getEditorComponent() {
        return datatypeSelector;
    }

    public OWLDatatype getEditedObject() {
        return datatypeSelector.getSelectedObject();
    }

    public boolean setEditedObject(OWLDatatype editedObject) {
        datatypeSelector.setSelection(editedObject);
        return true;
    }

    public void dispose() {
        datatypeSelector.dispose();
    }

    
}
