package org.protege.editor.owl.ui.frame;

import javax.swing.JComponent;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.semanticweb.owl.model.OWLObjectProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLObjectPropertyEditor extends AbstractOWLFrameSectionRowObjectEditor<OWLObjectProperty> {

    private OWLObjectPropertySelectorPanel editor;


    public OWLObjectPropertyEditor(OWLEditorKit owlEditorKit) {
        editor = new OWLObjectPropertySelectorPanel(owlEditorKit);
    }


    public OWLObjectProperty getEditedObject() {
        return editor.getSelectedOWLObjectProperty();
    }


    public JComponent getEditorComponent() {
        return editor;
    }


    public void clear() {
    }


    public void dispose() {
        editor.dispose();
    }
}
