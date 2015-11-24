package org.protege.editor.owl.model.search.nci;

import org.protege.editor.owl.model.search.SearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class SearchResultManager {

    private List<SearchResultSetWrapper> searchResultCache = new ArrayList<>();

    public void addSearchResults(Set<SearchResult> searchResults, boolean isLinked) {
        SearchResultSetWrapper searchResultSet = new SearchResultSetWrapper(searchResults, isLinked);
        searchResultCache.add(searchResultSet);
    }

    public Set<SearchResult> getSearchResults() {
        ImmutableSet.Builder<SearchResult> builder = new ImmutableSet.Builder<>();
        Set<SearchResult> unionResultSet = new HashSet<>();
        Set<EntityOnlySearchResult> intersectionResultSet = new HashSet<>();
        boolean nullIntersection = false;
        for (SearchResultSetWrapper searchResultSet : searchResultCache) {
            Set<SearchResult> resultSet = searchResultSet.getResultSet();
            if (searchResultSet.isLinked()) {
                if (nullIntersection) continue;
                if (intersectionResultSet.isEmpty()) {
                    intersectionResultSet.addAll(toEntityOnlyResultSet(resultSet));
                } else {
                    innerJoin(intersectionResultSet, toEntityOnlyResultSet(resultSet));
                }
                if (intersectionResultSet.isEmpty()) { nullIntersection = true; }
            } else {
                unionResultSet.addAll(resultSet);
            }
        }
        // Collect all the results
        collectSearchResults(builder, unionResultSet);
        collectSearchResults(builder, intersectionResultSet);
        return builder.build();
    }

    private void innerJoin(Set<EntityOnlySearchResult> set1, Set<EntityOnlySearchResult> set2) {
        Set<EntityOnlySearchResult> buffer = new HashSet<>();
        buffer.addAll(Sets.intersection(set1, set2));
        set1.clear();
        set1.addAll(buffer);
    }

    private static Set<EntityOnlySearchResult> toEntityOnlyResultSet(Set<SearchResult> resultSet) {
        Set<EntityOnlySearchResult> toReturn = new HashSet<>();
        for (SearchResult searchResult : resultSet) {
            toReturn.add(new EntityOnlySearchResult(searchResult));
        }
        return toReturn;
    }

    private void collectSearchResults(ImmutableSet.Builder<SearchResult> builder, Set<? extends SearchResult> resultSet) {
        builder.addAll(resultSet);
    }

    public class SearchResultSetWrapper {

        private Set<SearchResult> results;
        private boolean isLinked = false;

        public SearchResultSetWrapper(Set<SearchResult> results, boolean isLinked) {
            this.results = new HashSet<SearchResult>(results);
            this.isLinked = isLinked;
        }

        public boolean isLinked() {
            return isLinked;
        }

        public void setLink(boolean isLinked) {
            this.isLinked = isLinked;
        }

        public Set<SearchResult> getResultSet() {
            return Collections.unmodifiableSet(results);
        }
    }
}
