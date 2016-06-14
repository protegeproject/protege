package org.protege.editor.owl.model.search;

import java.util.Collection;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 19/09/2012
 */
public interface SearchResultHandler {

    void searchFinished(Collection<SearchResult> searchResults);
}
