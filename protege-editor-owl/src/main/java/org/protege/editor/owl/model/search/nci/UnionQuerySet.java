package org.protege.editor.owl.model.search.nci;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UnionQuerySet implements Iterable<AbstractQuerySet> {

    private List<AbstractQuerySet> unionQuery = new ArrayList<>();

    public UnionQuerySet() {
        // NO-OP
    }

    public void add(AbstractQuerySet querySet) {
        unionQuery.add(querySet);
    }

    @Override
    public Iterator<AbstractQuerySet> iterator() {
        return unionQuery.iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean needSeparator = false;
        for (AbstractQuerySet querySet : this) {
            if (needSeparator) {
                sb.append("\n");
            }
            sb.append("... Query: ").append(querySet);
            needSeparator = true;
        }
        return sb.toString();
    }
}
