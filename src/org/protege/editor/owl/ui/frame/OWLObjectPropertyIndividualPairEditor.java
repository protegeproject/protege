package org.protege.editor.owl.ui.frame;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLIndividualSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLObjectPropertyIndividualPairEditor extends AbstractOWLFrameSectionRowObjectEditor<OWLObjectPropertyIndividualPair> {

    private JPanel editorPanel;

    private OWLObjectPropertySelectorPanel objectPropertyPanel;

    private OWLIndividualSelectorPanel individualSelectorPanel;


    public OWLObjectPropertyIndividualPairEditor(OWLEditorKit owlEditorKit) {
        editorPanel = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        objectPropertyPanel = new OWLObjectPropertySelectorPanel(owlEditorKit);
        splitPane.setLeftComponent(objectPropertyPanel);
        individualSelectorPanel = new OWLIndividualSelectorPanel(owlEditorKit);
        splitPane.setRightComponent(individualSelectorPanel);
        editorPanel.add(splitPane);
    }


    public void setObjectPropertyIndividual(OWLObjectPropertyAssertionAxiom ax) {

    }


    public void clear() {
    }


    public OWLObjectPropertyIndividualPair getEditedObject() {
        return new OWLObjectPropertyIndividualPair(objectPropertyPanel.getSelectedOWLObjectProperty(),
                                                   individualSelectorPanel.getSelectedIndividual());
    }


    public Set<OWLObjectPropertyIndividualPair> getEditedObjects() {
        Set<OWLObjectPropertyIndividualPair> pairs = new HashSet<OWLObjectPropertyIndividualPair>();
        for (OWLObjectProperty prop : objectPropertyPanel.getSelectedOWLObjectProperties()) {
            for (OWLIndividual ind : individualSelectorPanel.getSelectedIndividuals()) {
                pairs.add(new OWLObjectPropertyIndividualPair(prop, ind));
            }
        }
        return pairs;
    }


    public JComponent getEditorComponent() {
        return editorPanel;
    }


    public void dispose() {
        objectPropertyPanel.dispose();
        individualSelectorPanel.dispose();
    }
}
