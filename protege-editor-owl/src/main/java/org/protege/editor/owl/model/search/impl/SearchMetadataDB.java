package org.protege.editor.owl.model.search.impl;

import org.protege.editor.owl.model.search.SearchMetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/10/2012
 */
public class SearchMetadataDB {

    private List<SearchMetadata> results = new ArrayList<SearchMetadata>();

    public void addResult(SearchMetadata searchMetadata) {
        results.add(searchMetadata);
    }

    public List<SearchMetadata> getResults() {
        return Collections.unmodifiableList(results);
    }
}
