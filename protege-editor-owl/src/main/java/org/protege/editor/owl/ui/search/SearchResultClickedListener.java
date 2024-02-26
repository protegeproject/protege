package org.protege.editor.owl.ui.search;

import java.awt.event.MouseEvent;

import org.protege.editor.owl.model.search.SearchResult;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31/03/15
 */
public interface SearchResultClickedListener {

    void handleSearchResultClicked(SearchResult searchResult, MouseEvent e);
}
