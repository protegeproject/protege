package org.protege.editor.owl.ui.explanation.io;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.editorkit.EditorKitPluginInstance;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;

public class InconsistentOntologyManager implements EditorKitPluginInstance {
	public static final Logger LOGGER = Logger.getLogger(InconsistentOntologyManager.class);
	
	private OWLEditorKit owlEditorKit;
	private OWLModelManagerListener listener = new OWLModelManagerListener() {
		public void handleChange(OWLModelManagerChangeEvent event) {
			if (event.getType() == EventType.ONTOLOGY_INCONSISTENT) {
				handleInconsistency();
			}
		}
	};


	public void setup(EditorKit editorKit) {
		this.owlEditorKit = (OWLEditorKit) editorKit;
		OWLModelManager p4Manager = owlEditorKit.getModelManager();
		p4Manager.put(InconsistentOntologyManager.class, this);
		p4Manager.addListener(listener);
	}
	
	private void handleInconsistency()  {
		InconsistentOntologyPluginLoader loader= new InconsistentOntologyPluginLoader(owlEditorKit);
		for (InconsistentOntologyPlugin plugin : loader.getPlugins()) {
			try {
				LOGGER.info("Found plugin " + plugin.newInstance().getClass());
			}
			catch (Exception e) {
				ProtegeApplication.getErrorLog().logError(e);
			}
		}
	}
	

	public void initialise() throws Exception {
	}

	public void dispose() throws Exception {
		OWLModelManager p4Manager = owlEditorKit.getModelManager();
		p4Manager.removeListener(listener);
	}

}
