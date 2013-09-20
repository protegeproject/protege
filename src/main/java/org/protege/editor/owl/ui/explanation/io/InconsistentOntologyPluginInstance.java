package org.protege.editor.owl.ui.explanation.io;

import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLOntology;

public interface InconsistentOntologyPluginInstance extends ProtegePluginInstance {

	void setup(OWLEditorKit editorKit);

	void explain(OWLOntology ontology);
}
