package org.protege.editor.owl.model.search;

import org.protege.editor.core.Disposable;
import org.semanticweb.owlapi.util.ProgressMonitor;

import java.util.Collection;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 09/02/16
 */
public interface SearchManager extends Disposable {

    void addProgressMonitor(ProgressMonitor pm);

    void dispose();

    boolean isSearchType(SearchCategory category);

    void setCategories(Collection<SearchCategory> categories);

    void performSearch(SearchRequest searchRequest, SearchResultHandler searchResultHandler);
}
