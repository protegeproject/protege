package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.SearchKeyword;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 10/11/2015
 */
public abstract class SearchQueryBuilder {

    public abstract boolean isBuilderFor(SearchKeyword keyword);

    public abstract void add(SearchKeyword keyword);

    public abstract SearchQuery build();
}
