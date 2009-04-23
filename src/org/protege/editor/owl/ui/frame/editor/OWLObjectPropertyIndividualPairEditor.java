package org.protege.editor.owl.ui.frame.editor;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.OWLObjectPropertyIndividualPair;
import org.protege.editor.owl.ui.selector.OWLIndividualSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLPropertyAssertionAxiom;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br>
 * <br>
 */
public class OWLObjectPropertyIndividualPairEditor extends
		AbstractOWLFrameSectionRowObjectEditor<OWLObjectPropertyIndividualPair> {
	private JPanel editorPanel;
	private OWLObjectPropertySelectorPanel objectPropertyPanel;
	private OWLIndividualSelectorPanel individualSelectorPanel;

	public OWLObjectPropertyIndividualPairEditor(OWLEditorKit owlEditorKit) {
		this.editorPanel = new JPanel(new BorderLayout());
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.objectPropertyPanel = new OWLObjectPropertySelectorPanel(
				owlEditorKit);
		splitPane.setLeftComponent(this.objectPropertyPanel);
		this.individualSelectorPanel = new OWLIndividualSelectorPanel(
				owlEditorKit, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		splitPane.setRightComponent(this.individualSelectorPanel);
		this.editorPanel.add(splitPane);
	}

	public void setObjectPropertyAxiom(
			OWLPropertyAssertionAxiom<OWLObjectPropertyExpression, OWLIndividual> ax) {
		OWLObjectPropertyExpression p = ax.getProperty();
		if (p instanceof OWLObjectProperty) {
			this.objectPropertyPanel.setSelection((OWLObjectProperty) p);
		}
        // @@TODO v3 port
        if (!ax.getObject().isAnonymous()){
            this.individualSelectorPanel.setSelection(ax.getObject().asNamedIndividual());
        }
    }

	public void clear() {
	}

	public OWLObjectPropertyIndividualPair getEditedObject() {
		return new OWLObjectPropertyIndividualPair(this.objectPropertyPanel
				.getSelectedObject(), this.individualSelectorPanel
				.getSelectedObject());
	}

	@Override
	public Set<OWLObjectPropertyIndividualPair> getEditedObjects() {
		Set<OWLObjectPropertyIndividualPair> pairs = new HashSet<OWLObjectPropertyIndividualPair>();
		for (OWLObjectProperty prop : this.objectPropertyPanel.getSelectedObjects()) {
			for (OWLIndividual ind : this.individualSelectorPanel.getSelectedObjects()) {
				pairs.add(new OWLObjectPropertyIndividualPair(prop, ind));
			}
		}
		return pairs;
	}

	public JComponent getEditorComponent() {
		return this.editorPanel;
	}

	public void dispose() {
		this.objectPropertyPanel.dispose();
		this.individualSelectorPanel.dispose();
	}
}
