package org.protege.editor.owl.model.search.nci;

import org.protege.editor.owl.model.search.lucene.SearchQuery;

import com.google.common.collect.ImmutableSet;

public class QuerySet extends AbstractQuerySet {

    protected ImmutableSet.Builder<SearchQuery> builder = new ImmutableSet.Builder<>();

    public void add(SearchQuery query) {
        builder.add(query);
    }

    public ImmutableSet<SearchQuery> getQueries() {
        return builder.build();
    }

    @Override
    public boolean isLinked() {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean needSeparator = false;
        for (SearchQuery searchQuery : getQueries()) {
            if (needSeparator) {
                sb.append("; ");
            }
            sb.append(searchQuery);
            needSeparator = true;
        }
        return sb.toString();
    }
}
