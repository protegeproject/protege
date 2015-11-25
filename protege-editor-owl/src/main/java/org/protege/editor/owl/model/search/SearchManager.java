package org.protege.editor.owl.model.search;

import org.protege.editor.core.Disposable;

import org.semanticweb.owlapi.util.Monitorable;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/11/2015
 */
public interface SearchManager extends Monitorable, Disposable {

    SearchSettings getSearchSettings();

    void performSearch(String searchString, SearchResultHandler searchResultHandler);
}
