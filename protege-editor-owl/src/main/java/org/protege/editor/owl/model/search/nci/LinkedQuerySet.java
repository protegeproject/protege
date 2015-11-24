package org.protege.editor.owl.model.search.nci;

import java.util.Iterator;

import com.google.common.collect.ImmutableList;

public class LinkedQuerySet extends AbstractQuerySet implements Iterable<QuerySet> {

    private ImmutableList.Builder<QuerySet> builder = new ImmutableList.Builder<>();
    
    public void add(QuerySet querySet) {
        builder.add(querySet);
    }

    @Override
    public boolean isLinked() {
        return true;
    }

    @Override
    public Iterator<QuerySet> iterator() {
        return builder.build().iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean needSeparator = false;
        for (QuerySet querySet : this) {
            if (needSeparator) {
                sb.append("\n");
                sb.append("      & ");
            }
            sb.append(querySet);
            needSeparator = true;
        }
        return sb.toString();
    }
}
