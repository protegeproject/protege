package org.protege.editor.owl.ui.editor;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLObjectPropertyEditor extends AbstractOWLObjectEditor<OWLObjectProperty> {

    private OWLObjectPropertySelectorPanel editor;


    public OWLObjectPropertyEditor(OWLEditorKit owlEditorKit) {
        editor = new OWLObjectPropertySelectorPanel(owlEditorKit);
    }


    public OWLObjectProperty getEditedObject() {
        return editor.getSelectedObject();
    }

    public boolean setEditedObject(OWLObjectProperty p){
        editor.setSelection(p);
        return true;
    }


    public String getEditorTypeName() {
        return "Object property";
    }


    public boolean canEdit(Object object) {
        return object instanceof OWLObjectProperty;
    }


    public JComponent getEditorComponent() {
        return editor;
    }


    public void dispose() {
        editor.dispose();
    }
}
