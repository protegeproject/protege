package org.protege.editor.owl.ui.explanation;

import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExplanationManager implements Disposable {

	private final Logger logger = LoggerFactory.getLogger(ExplanationManager.class);

	private final OWLEditorKit editorKit;

	private final Collection<ExplanationService> explanationServices = new HashSet<>();
	
	private final Collection<ExplanationDialog> openedExplanations = new HashSet<>();
	
	public ExplanationManager(OWLEditorKit editorKit) {
		this.editorKit = editorKit;
		reload();
	}

	public void reload() {
		ExplanationPluginLoader loader = new ExplanationPluginLoader(editorKit);
		explanationServices.clear();
		for (ExplanationPlugin plugin : loader.getPlugins()) {
			ExplanationService teacher = null;
			try {
				teacher = plugin.newInstance();
				teacher.initialise();
				explanationServices.add(teacher);
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
		return explanationServices;
	}
	
	public Collection<ExplanationService> getTeachers(OWLAxiom axiom) {
		Set<ExplanationService> smartTeachers = new HashSet<>();
		for (ExplanationService teacher : explanationServices) {
			if (teacher.hasExplanation(axiom)) {
				smartTeachers.add(teacher);
			}
		}
		return smartTeachers;
	}
	
	public boolean hasExplanation(OWLAxiom axiom) {
		for (ExplanationService explanationService : explanationServices) {
			if (explanationService.hasExplanation(axiom)) {
				return true;
			}
		}
		return false;
	}
	
	public void handleExplain(Frame owner, OWLAxiom axiom) {
		final ExplanationDialog explanation = new ExplanationDialog(this, axiom);
		openedExplanations.add(explanation);
		JOptionPane op = new JOptionPane(explanation, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
        JDialog dlg = op.createDialog(owner, getExplanationDialogTitle(axiom));
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose(explanation);
			}			
		});
        dlg.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
            	dispose(explanation);
            }
        });
        dlg.setModal(false);
        dlg.setResizable(true);
        dlg.pack();
        dlg.setVisible(true);
	}
	
	private void dispose(ExplanationDialog explanation) {
		if (openedExplanations.remove(explanation)) {
			explanation.dispose();
		}
	}

    private String getExplanationDialogTitle(OWLAxiom entailment) {
        String rendering = editorKit.getOWLModelManager().getRendering(entailment).replaceAll("\\s", " ");
        return "Explanation for " + rendering;
    }

	@Override
	public void dispose() throws Exception {
		for (ExplanationDialog explanation : openedExplanations) {
			explanation.dispose();
		}
		openedExplanations.clear();
		for (ExplanationService teacher : explanationServices) {
			teacher.dispose();
		}
	}
}
