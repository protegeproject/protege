package org.protege.editor.owl.ui.editor;

import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;

public class IRITextEditor implements OWLObjectEditor<IRI>, VerifiedInputEditor {
	private OWLObjectEditorHandler<IRI> handler;
	private JPanel editor;
	private JTextField iriTextField;
	private List<InputVerificationStatusChangedListener> inputVerificationListeners = new ArrayList<InputVerificationStatusChangedListener>();
	
	
	public IRITextEditor(OWLEditorKit editorKit) {
		createGui();
		setInitialIri(editorKit);
	}
	
	private void createGui() {
		editor = new JPanel();
		editor.setLayout(new FlowLayout());
		editor.add(new JLabel("IRI: "));
		iriTextField = new JTextField();
		iriTextField.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				for (InputVerificationStatusChangedListener listener : inputVerificationListeners) {
					listener.verifiedStatusChanged(getEditedObject() != null);
				}
			}
			
			public void keyPressed(KeyEvent e) {

			}
			public void keyReleased(KeyEvent e) {

			}
		});
		editor.add(iriTextField);
	}
	
	private void setInitialIri(OWLEditorKit editorKit) {
		OWLOntology ontology = editorKit.getOWLModelManager().getActiveOntology();
		OWLOntologyID ontologyId = ontology.getOntologyID();
		if (!ontologyId.isAnonymous()) {
			iriTextField.setText(ontologyId.getOntologyIRI().toString());
		}
	}

	public String getEditorTypeName() {
		return "IRI Editor";
	}

	public boolean canEdit(Object object) {
		return object instanceof IRI;
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

	public boolean setEditedObject(IRI editedObject) {
		if (editedObject != null && editedObject instanceof IRI) {
			iriTextField.setText(editedObject.toString());
		}
		return editedObject != null;
	}

	public IRI getEditedObject() {
		IRI editedObject = null;
		try {
			editedObject = IRI.create(iriTextField.getText());
		}
		catch (RuntimeException e) {
			;
		}
		return editedObject;
	}

	public Set<IRI> getEditedObjects() {
		IRI editedObject = getEditedObject();
		if (editedObject != null) {
			return Collections.singleton(editedObject);
		}
		else {
			return Collections.emptySet();
		}
	}
	
	public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
		inputVerificationListeners.add(listener);
	}
	
	public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
		inputVerificationListeners.remove(listener);
	}

	public OWLObjectEditorHandler<IRI> getHandler() {
		return handler;
	}

	public void setHandler(OWLObjectEditorHandler<IRI> handler) {
		this.handler = handler;
	}

	public void clear() {
	}

	public void dispose() {
	}

}
