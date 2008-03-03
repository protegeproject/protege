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
 * Date: 09-Feb-2007<br>
 * <br>
 */
public class OWLIndividualEditor extends
		AbstractOWLFrameSectionRowObjectEditor<OWLIndividual> {
	private OWLIndividualSelectorPanel selectorPanel;

	public OWLIndividualEditor(OWLEditorKit owlEditorKit) {
		this.selectorPanel = new OWLIndividualSelectorPanel(owlEditorKit);
	}

	/**
	 * Builds an OWLIndividualEditor instance with the input selection mode The
	 * legal values are the same of the ListSelectionModel constants and the
	 * default value is ListSelectionModel.SINGLE_SELECTION
	 * 
	 * @param owlEditorKit
	 * @param selectionMode
	 */
	public OWLIndividualEditor(OWLEditorKit owlEditorKit, int selectionMode) {
		this.selectorPanel = new OWLIndividualSelectorPanel(owlEditorKit,
				selectionMode);
	}

	/**
	 * Gets a component that will be used to edit the specified object.
	 * 
	 * @return The component that will be used to edit the object
	 */
	public JComponent getEditorComponent() {
		return this.selectorPanel;
	}

	public JComponent getInlineEditorComponent() {
		return this.selectorPanel;
	}

	public void clear() {
	}

	/**
	 * Gets the object that has been edited.
	 * 
	 * @return The edited object
	 */
	public OWLIndividual getEditedObject() {
		return this.selectorPanel.getSelectedIndividual();
	}

	@Override
	public Set<OWLIndividual> getEditedObjects() {
		return this.selectorPanel.getSelectedIndividuals();
	}

	public void dispose() {
		this.selectorPanel.dispose();
	}
}
