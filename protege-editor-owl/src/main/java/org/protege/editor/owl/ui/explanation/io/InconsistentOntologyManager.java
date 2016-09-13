package org.protege.editor.owl.ui.explanation.io;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLEditorKitHook;
import org.protege.editor.owl.model.OWLModelManager;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class InconsistentOntologyManager extends OWLEditorKitHook  {

	public static final String EXPLAIN = "Explain";

	private InconsistentOntologyPlugin lastSelectedPlugin;

	private final List<InconsistentOntologyPluginInstance> explanations = new ArrayList<>();

	public static InconsistentOntologyManager get(OWLModelManager modelManager) {
		return (InconsistentOntologyManager) modelManager.get(InconsistentOntologyManager.class);
	}

	public void initialise() throws Exception {
		OWLEditorKit editorKit = getEditorKit();
		OWLModelManager p4Manager = editorKit.getModelManager();
		p4Manager.put(InconsistentOntologyManager.class, this);
	}

	public void explain()  {
		try {
			Object[] options = new Object[] {EXPLAIN, "Cancel" };
			final OWLEditorKit owlEditorKit = getEditorKit();
			IntroductoryPanel intro = new IntroductoryPanel(owlEditorKit, lastSelectedPlugin);
			int ret = JOptionPane.showOptionDialog(owlEditorKit.getOWLWorkspace(), 
							 				  	   intro, "Help for inconsistent ontologies", 
							 				  	   JOptionPane.YES_NO_CANCEL_OPTION,
							 				  	   JOptionPane.QUESTION_MESSAGE,
					null,
							 				  	   options, EXPLAIN);
			if (ret == 0) {
				lastSelectedPlugin = intro.getSelectedPlugin();
				InconsistentOntologyPluginInstance i = lastSelectedPlugin.newInstance();
				i.initialise();
				i.setup(owlEditorKit);
				i.explain(owlEditorKit.getOWLModelManager().getActiveOntology());
				explanations.add(i);
			}
		}
		catch (Exception ioe) {
			LoggerFactory.getLogger(InconsistentOntologyManager.class)
					.warn("An error occurred whilst generating an explanation for the inconsistent ontology: {}", ioe);
		}
	}

	public void dispose() throws Exception {
		for (InconsistentOntologyPluginInstance e : explanations) {
			e.dispose();
		}
		explanations.clear();
	}

}
