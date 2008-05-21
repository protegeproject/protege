package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLIndividualSelectorPanel;
import org.semanticweb.owl.model.OWLIndividual;

import javax.swing.*;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLIndividualSetEditor extends AbstractOWLFrameSectionRowObjectEditor<Set<OWLIndividual>> {

    private OWLIndividualSelectorPanel panel;


    public OWLIndividualSetEditor(OWLEditorKit owlEditorKit) {
        panel = new OWLIndividualSelectorPanel(owlEditorKit);
    }


    public Set<OWLIndividual> getEditedObject() {
        return panel.getSelectedObjects();
    }


    public JComponent getEditorComponent() {
        return panel;
    }


    public JComponent getInlineEditorComponent() {
        return null;
    }


    public void dispose() {
        panel.dispose();
    }


    public void clear() {
    }
}
