package org.protege.editor.owl.ui.explanation.io;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLEditorKitHook;
import org.protege.editor.owl.model.OWLModelManager;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class InconsistentOntologyManager extends OWLEditorKitHook  {

	public static final String EXPLAIN = "Explain";
	
	private OWLEditorKit owlEditorKit;
	private InconsistentOntologyPlugin lastSelectedPlugin;
	private List<InconsistentOntologyPluginInstance> explanations = new ArrayList<InconsistentOntologyPluginInstance>();

	public static InconsistentOntologyManager get(OWLModelManager modelManager) {
		return (InconsistentOntologyManager) modelManager.get(InconsistentOntologyManager.class);
	}
	
	public void setup(EditorKit editorKit) {
		this.owlEditorKit = (OWLEditorKit) editorKit;
		OWLModelManager p4Manager = owlEditorKit.getModelManager();
		p4Manager.put(InconsistentOntologyManager.class, this);
	}
	
	public void explain()  {
		try {
			Object[] options = new Object[] {EXPLAIN, "Cancel" };
			IntroductoryPanel intro = new IntroductoryPanel(owlEditorKit, lastSelectedPlugin);
			int ret = JOptionPane.showOptionDialog(owlEditorKit.getOWLWorkspace(), 
							 				  	   intro, "Help for inconsistent ontologies", 
							 				  	   JOptionPane.YES_NO_CANCEL_OPTION,
							 				  	   JOptionPane.QUESTION_MESSAGE,
							 				  	   (Icon) null,
							 				  	   options, EXPLAIN);
			if (ret == 0) {
				lastSelectedPlugin = intro.getSelectedPlugin();
				InconsistentOntologyPluginInstance i = lastSelectedPlugin.newInstance();
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
	

	public void initialise() throws Exception {
	}

	public void dispose() throws Exception {
		for (InconsistentOntologyPluginInstance e : explanations) {
			e.dispose();
		}
		explanations.clear();
	}

}
