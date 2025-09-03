package org.protege.editor.owl.model.search;

import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.util.ProgressMonitor;

import java.util.Collection;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 09/02/16
 */
public abstract class SearchManager implements ProtegePluginInstance {

    private OWLEditorKit editorKit = null;

    public final void setup(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
    }

    public OWLEditorKit getEditorKit() {
        if(editorKit == null) {
            throw new RuntimeException("Not set up correctly");
        }
        return editorKit;
    }

    public abstract void dispose();

    public abstract void addProgressMonitor(ProgressMonitor pm);

    public abstract boolean isSearchType(SearchCategory category);

    public abstract void setCategories(Collection<SearchCategory> categories);

    public abstract void performSearch(SearchRequest searchRequest, SearchResultHandler searchResultHandler);
}
