package org.protege.editor.owl.ui.explanation;

import java.awt.Frame;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLAxiom;

public class ExplanationManager {
	private static Logger logger = Logger.getLogger(ExplanationManager.class);
	private OWLEditorKit editorKit;
	private Collection<ExplanationService> explainers = new HashSet<ExplanationService>();
	
	public ExplanationManager(OWLEditorKit editorKit) {
		this.editorKit = editorKit;
		reload();
	}
	
	public void reload() {
		ExplanationPluginLoader loader = new ExplanationPluginLoader(editorKit);
		explainers.clear();
		for (ExplanationPlugin plugin : loader.getPlugins()) {
			ExplanationService teacher = null;
			try {
				teacher = plugin.newInstance();
				teacher.initialise();
				explainers.add(teacher);
			}
			catch (Exception e) {
				logger.warn("Exception caught initialising explanation service " + plugin.getName() + " - skipping...", e);
			}
		}
	}
	
	public OWLEditorKit getOWLEditorKit() {
		return editorKit;
	}
	
	public OWLModelManager getModelManager() {
		return editorKit.getModelManager();
	}
	
	public Collection<ExplanationService> getExplainers() {
		return explainers;
	}
	
	public Collection<ExplanationService> getTeachers(OWLAxiom axiom) {
		Set<ExplanationService> smartTeachers = new HashSet<ExplanationService>();
		for (ExplanationService teacher : explainers) {
			if (teacher.hasExplanation(axiom)) {
				smartTeachers.add(teacher);
			}
		}
		return smartTeachers;
	}
	
	public boolean hasExplanation(OWLAxiom axiom) {
		for (ExplanationService teacher : explainers) {
			if (teacher.hasExplanation(axiom)) {
				return true;
			}
		}
		return false;
	}
	
	public void handleExplain(Frame owner, OWLAxiom axiom) {
		Collection<ExplanationService> teachers = getTeachers(axiom);
		JDialog explanation = new ExplanationPanel(owner, this, axiom);
		explanation.setVisible(true);
	}
}
