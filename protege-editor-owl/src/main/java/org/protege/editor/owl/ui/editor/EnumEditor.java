package org.protege.editor.owl.ui.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;

public class EnumEditor implements OWLObjectEditor<OWLLiteral> {
	private OWLObjectEditorHandler<OWLLiteral> handler;
	private JPanel editor;
	private JLabel label = new JLabel("Enum:");
	private JComboBox<String> enumCombo;
	private OWLEditorKit kit = null;
	
	public EnumEditor(OWLEditorKit editorKit) {
		kit = editorKit;
		createGui();
	}
	
	private void createGui() {
		editor = new JPanel();
		editor.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(12,12,0,12);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 0;
		c.weighty = 0;
		editor.add(label, c);
		
		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(12,0,0,12);
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		enumCombo = new JComboBox<String>();
		editor.add(enumCombo, c);
	}
	
	public String getEditorTypeName() {
		return "Enum Selector";
	}

	public boolean canEdit(Object object) {
		return object instanceof OWLLiteral;
	}

	public boolean isMultiEditSupported() {
		return false;
	}
	
	public boolean isPreferred() {
		return false;
	}

	public JComponent getEditorComponent() {
		return editor;
	}
	
	public void setProp(OWLAnnotationProperty prop) {

		IRI iri = getDataType(prop);
		if (iri != null) {
			label.setText(iri.getShortForm());
			List<String> vals = getEnumValues(iri);
			if (!vals.isEmpty()) {
				enumCombo.removeAllItems();
				for (String s : vals) {
					enumCombo.addItem(s);
				}
			}
		}
	}

	public boolean setEditedObject(OWLLiteral editedObject) {
		if (editedObject != null) {
			enumCombo.setSelectedItem(editedObject.asLiteral().get().getLiteral());
		}
		return true;
	}

	public OWLLiteral getEditedObject() {
		String s = (String) enumCombo.getSelectedItem();
		if (s != null) {
			return kit.getOWLModelManager().getOWLDataFactory().getOWLLiteral(s);
		}
		return null;
	}

	public Set<OWLLiteral> getEditedObjects() {
		return Collections.emptySet();
		
	}
	

	public OWLObjectEditorHandler<OWLLiteral> getHandler() {
		return handler;
	}

	public void setHandler(OWLObjectEditorHandler<OWLLiteral> handler) {
		this.handler = handler;
	}

	public void clear() {
		//iriTextField.setText("");
	}

	public void dispose() {
	}
	
	private List<String> getEnumValues(IRI enumtype) {

		List<String> results = new ArrayList<String>();
		OWLDatatype dt = kit.getOWLModelManager().getOWLDataFactory().getOWLDatatype(enumtype);		
		Set<OWLDatatypeDefinitionAxiom> dda = kit.getOWLModelManager().getActiveOntology().getDatatypeDefinitions(dt);

		for (OWLDatatypeDefinitionAxiom ax : dda) {
			OWLDataOneOf done = (OWLDataOneOf) ax.getDataRange();
			Set<OWLLiteral> vals = done.getValues();
			for (OWLLiteral lit : vals) {
				results.add(lit.getLiteral());
			}

		}
		return results;
	}
	
	public IRI getDataType(OWLAnnotationProperty prop) {
		Set<OWLAnnotationPropertyRangeAxiom> types = 
				kit.getOWLModelManager().getActiveOntology().getAnnotationPropertyRangeAxioms(prop);
		
		for (OWLAnnotationPropertyRangeAxiom ax : types) {
			return ax.getRange();
		}
		return null;
	}
	
	public boolean isDataTypeCombobox( IRI dataType ) {
		boolean result = false;
		// kind of a kluge, use a convention that a datatype name ending with the suffix
		// enum is in fact an enum, and we can look ad the property range to get the enum values
		if (dataType.toString().endsWith("enum")) {
			result = true;
		}
		return result;
	}
}
