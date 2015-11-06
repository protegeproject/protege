package org.protege.editor.owl.ui.explanation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ExplanationManager {

	private final Logger logger = LoggerFactory.getLogger(ExplanationManager.class);

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
				logger.error("An error occurred whilst initialising an explanation service {}.", plugin.getName(), e);
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
		final ExplanationDialog explanation = new ExplanationDialog(this, axiom);
		JOptionPane op = new JOptionPane(explanation, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
        JDialog dlg = op.createDialog(owner, getExplanationDialogTitle(axiom));
        dlg.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                explanation.dispose();
            }
        });
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setModal(false);
        dlg.setResizable(true);
        dlg.pack();
        dlg.setVisible(true);
	}

    private String getExplanationDialogTitle(OWLAxiom entailment) {
        String rendering = editorKit.getOWLModelManager().getRendering(entailment).replaceAll("\\s", " ");
        return "Explanation for " + rendering;
    }
}
