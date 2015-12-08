package org.protege.editor.owl.model.search.lucene;

import java.util.Iterator;

import com.google.common.collect.ImmutableList;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 06/11/2015
 */
public class SearchQueries implements Iterable<SearchQuery> {

    private ImmutableList.Builder<SearchQuery> builder = new ImmutableList.Builder<>();

    public void add(SearchQuery query) {
        builder.add(query);
    }

    public ImmutableList<SearchQuery> getQueries() {
        return builder.build();
    }

    public int size() {
        return getQueries().size();
    }

    @Override
    public Iterator<SearchQuery> iterator() {
        return getQueries().iterator();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        boolean needNewline = false;
        for (SearchQuery query : getQueries()) {
            if (needNewline) {
                sb.append("\n");
            }
            sb.append(query.toString());
            needNewline = true;
        }
        return sb.toString();
    }
}
