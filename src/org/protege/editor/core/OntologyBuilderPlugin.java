package org.protege.editor.core;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractProtegePlugin;

public class OntologyBuilderPlugin extends AbstractProtegePlugin<OntologyBuilder> {
    public static final String ID = "OntologyLoader";
    public static final String EDITOR_KIT_PARAM = "editorKit";
    public static final String NAME_PARAM = "name";

    protected OntologyBuilderPlugin(IExtension extension) {
        super(extension);
    }
    
    public String getEditorKitId() {
        return getPluginProperty(EDITOR_KIT_PARAM, null);
    }
    
}
