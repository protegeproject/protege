package org.protege.editor.owl.ui.explanation;

import javax.swing.JComponent;

import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLAxiom;

public abstract class ExplanationService implements ProtegePluginInstance {
	private static final long serialVersionUID = -5422962627407903823L;
	private OWLEditorKit editorKit;
	private String pluginId;
	private String name;

	public void setup(OWLEditorKit editorKit, String pluginId, String name) {
		this.editorKit = editorKit;
		this.pluginId = pluginId;
		this.name = name;
	}
	
	public OWLEditorKit getOWLEditorKit() {
		return editorKit;
	}
	
	public OWLModelManager getOWLModelManager() {
		return editorKit.getModelManager();
	}
	
	public String getPluginId() {
		return pluginId;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public abstract void initialise() throws Exception;
	
	public abstract boolean hasExplanation(OWLAxiom axiom);
	
	public abstract JComponent explain(OWLAxiom axiom);
}
