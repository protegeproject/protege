package org.protege.editor.owl.model.search.lucene;

import org.apache.lucene.search.Query;

import java.util.Iterator;

import com.google.common.collect.ImmutableList;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 06/11/2015
 */
public class BatchQuery implements Iterable<Query> {

    private ImmutableList.Builder<Query> builder = new ImmutableList.Builder<>();

    public void add(Query query) {
        builder.add(query);
    }

    public ImmutableList<Query> getQueries() {
        return builder.build();
    }

    @Override
    public Iterator<Query> iterator() {
        return getQueries().iterator();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        boolean needNewline = false;
        for (Query query : getQueries()) {
            if (needNewline) {
                sb.append("\n");
            }
            sb.append(query.toString());
            needNewline = true;
        }
        return sb.toString();
    }
}
