package org.protege.editor.owl.ui.explanation;

import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

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

	private final Collection<ExplanationService> explanationServices = new ArrayList<>();

	private final Collection<ExplanationService> enabledServices = new ArrayList<>();

	private final Collection<ExplanationDialog> openedExplanations = new HashSet<>();
	
	public ExplanationManager(OWLEditorKit editorKit) {
		this.editorKit = editorKit;
		reload();
	}

	public void reload() {
		ExplanationPluginLoader loader = new ExplanationPluginLoader(editorKit);
		// use TreeMap for alphabetical ordering
		Map<String, ExplanationService> sortedExplanationServices = new TreeMap<>();
		for (ExplanationPlugin plugin : loader.getPlugins()) {
			ExplanationService teacher = null;
			try {
				teacher = plugin.newInstance();
				teacher.initialise();
				sortedExplanationServices.put(teacher.getPluginId(), teacher);
			} catch (Exception e) {
				logger.error("An error occurred whilst initialising an explanation service {}.", plugin.getName(), e);
			}
		}

		// add ExplanationServices in the order defined in the preferences
		final ExplanationPreferences prefs = ExplanationPreferences.create().load();
		explanationServices.clear();
		for (String id : prefs.explanationServicesList) {
			ExplanationService teacher = sortedExplanationServices.get(id);
			if (teacher != null) {
				explanationServices.add(teacher);
				sortedExplanationServices.remove(id);
			}
		}

		if (!sortedExplanationServices.isEmpty()) {
			// add new ExplanationServices (which do not occur in the preferences yet) in
			// alphabetical order at the end
			for (ExplanationService teacher : sortedExplanationServices.values()) {
				explanationServices.add(teacher);
			}
		}

		// update preferences according to current list (adding new and removing old
		// ExplanationServices)
		prefs.explanationServicesList = new ArrayList<>();
		for (ExplanationService teacher : explanationServices) {
			prefs.explanationServicesList.add(teacher.getPluginId());
		}
		prefs.save();

		enabledServices.clear();
		for (ExplanationService teacher : explanationServices) {
			if (!prefs.disabledExplanationServices.contains(teacher.getPluginId())) {
				enabledServices.add(teacher);
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
		Collection<ExplanationService> smartTeachers = new ArrayList<>();
		for (ExplanationService teacher : enabledServices) {
			if (teacher.hasExplanation(axiom)) {
				smartTeachers.add(teacher);
			}
		}
		return smartTeachers;
	}
	
	public boolean hasExplanation(OWLAxiom axiom) {
		for (ExplanationService explanationService : enabledServices) {
			if (explanationService.hasExplanation(axiom)) {
				return true;
			}
		}
		return false;
	}
	
	public void handleExplain(Frame owner, OWLAxiom axiom) {
		reload();
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
