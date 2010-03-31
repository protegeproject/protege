package org.protege.editor.core;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.ProtegePluginInstance;

public interface OntologyBuilder extends ProtegePluginInstance {
    
    boolean loadOntology(EditorKit editorKit);
}
