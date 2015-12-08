package org.protege.editor.owl.model.search.nci;

import org.protege.editor.owl.model.search.lucene.SearchQueries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class UserQueries implements Iterable<Entry<SearchQueries, Boolean>> {

    private Map<SearchQueries, Boolean> userQuerySettings = new HashMap<>();

    public void add(SearchQueries searchQueries, boolean isLinked) {
        userQuerySettings.put(searchQueries, isLinked);
    }

    @Override
    public Iterator<Entry<SearchQueries, Boolean>> iterator() {
        return userQuerySettings.entrySet().iterator();
    }
}
