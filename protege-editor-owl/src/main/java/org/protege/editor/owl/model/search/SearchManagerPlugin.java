package org.protege.editor.owl.model.search;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractProtegePlugin;
import org.protege.editor.owl.OWLEditorKit;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 09/02/16
 */
public class SearchManagerPlugin extends AbstractProtegePlugin<SearchManager> {

    public static final String ID = "searchmanager";

    private OWLEditorKit editorKit;

    public SearchManagerPlugin(IExtension extension, OWLEditorKit editorKit) {
        super(extension);
        this.editorKit = editorKit;
    }

    public String getName() {
        return getPluginProperty("name", "Search plugin (No Name Supplied)");
    }

    @Override
    public SearchManager newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        try {
            SearchManager searchManager =  super.newInstance();
            searchManager.setup(editorKit);
            return searchManager;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
