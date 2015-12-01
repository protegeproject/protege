package org.protege.editor.owl.model.search.nci;

import org.protege.editor.owl.model.search.lucene.SearchQuery;

import java.util.Iterator;

import com.google.common.collect.ImmutableSet;

public class QuerySet extends AbstractQuerySet implements Iterable<SearchQuery> {

    protected ImmutableSet.Builder<SearchQuery> builder = new ImmutableSet.Builder<>();

    public QuerySet() {
        // NO-OP
    }

    public QuerySet(SearchQuery query) {
        add(query);
    }

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
    public Iterator<SearchQuery> iterator() {
        return getQueries().iterator();
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
