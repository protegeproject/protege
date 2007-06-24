package org.protege.editor.owl.ui.frame;

import javax.swing.JComponent;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLDataPropertySelectorPanel;
import org.semanticweb.owl.model.OWLDataProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDataPropertyEditor extends AbstractOWLFrameSectionRowObjectEditor<OWLDataProperty> {

    private OWLDataPropertySelectorPanel editor;


    public OWLDataPropertyEditor(OWLEditorKit owlEditorKit) {
        editor = new OWLDataPropertySelectorPanel(owlEditorKit);
    }


    public OWLDataProperty getEditedObject() {
        return editor.getSelectedDataProperty();
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
