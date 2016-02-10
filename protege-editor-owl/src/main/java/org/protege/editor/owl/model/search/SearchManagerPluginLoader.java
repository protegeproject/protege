package org.protege.editor.owl.model.search;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ProtegeOWL;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 09/02/16
 */
public class SearchManagerPluginLoader extends AbstractPluginLoader<SearchManagerPlugin> {

    private OWLEditorKit editorKit;

    public SearchManagerPluginLoader(OWLEditorKit editorKit) {
        super(ProtegeOWL.ID, SearchManagerPlugin.ID);
        this.editorKit = editorKit;
    }

    @Override
    protected SearchManagerPlugin createInstance(IExtension extension) {
        SearchManagerPlugin plugin = new SearchManagerPlugin(extension, editorKit);
        return plugin;
    }
}
