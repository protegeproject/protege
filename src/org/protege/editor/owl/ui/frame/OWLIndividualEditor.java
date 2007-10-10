package org.protege.editor.owl.ui.frame;

import java.util.Set;

import javax.swing.JComponent;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLIndividualSelectorPanel;
import org.semanticweb.owl.model.OWLIndividual;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 09-Feb-2007<br><br>
 */
public class OWLIndividualEditor extends AbstractOWLFrameSectionRowObjectEditor<OWLIndividual> {

    private OWLIndividualSelectorPanel selectorPanel;


    public OWLIndividualEditor(OWLEditorKit owlEditorKit) {
        selectorPanel = new OWLIndividualSelectorPanel(owlEditorKit);
    }


    /**
     * Gets a component that will be used to edit the specified
     * object.
     * @return The component that will be used to edit the object
     */
    public JComponent getEditorComponent() {
        return selectorPanel;
    }


    public JComponent getInlineEditorComponent() {
        return selectorPanel;
    }


    public void clear() {
    }


    /**
     * Gets the object that has been edited.
     * @return The edited object
     */
    public OWLIndividual getEditedObject() {
        return selectorPanel.getSelectedIndividual();
    }


    public Set<OWLIndividual> getEditedObjects() {
        return selectorPanel.getSelectedIndividuals();
    }


    public void dispose() {
        selectorPanel.dispose();
    }
}
