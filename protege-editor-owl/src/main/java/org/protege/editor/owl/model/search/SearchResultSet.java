package org.protege.editor.owl.model.search;

import java.util.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 19/09/2012
 */
public class SearchResultSet {

    private List<SearchResult> searchResults;


    private Map<SearchCategoryGroupKey, List<SearchResult>> searchResultsByCategory;

    public SearchResultSet(Collection<SearchResult> searchResults) {
        this.searchResults = new ArrayList<>(searchResults);
        buildCatResults();
    }

    public int getCategoryResultsCount(String cat) {
        SearchCategoryGroupKey key = getKeyForCategory(cat);
        List<SearchResult> catResults = searchResultsByCategory.get(key);
        if (catResults == null) {
            return 0;
        }
        else {
            return catResults.size();
        }
    }

    public List<String> getCategories() {
        List<String> result = new ArrayList<String>();
        for (SearchCategoryGroupKey searchCategoryGroupKey : new TreeSet<SearchCategoryGroupKey>(searchResultsByCategory.keySet())) {
            result.add(searchCategoryGroupKey.subcategory);
        }
        return result;
    }

    public List<SearchResult> getCategoryResults(String category) {
        SearchCategoryGroupKey key = getKeyForCategory(category);
        List<SearchResult> catResults = searchResultsByCategory.get(key);
        if (catResults == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(catResults);
    }

    public List<SearchResult> getCategoryResults(String category, int limit) {
        List<SearchResult> trimmedResult = new ArrayList<>();
        SearchCategoryGroupKey key = getKeyForCategory(category);
        if (key == null) {
            return Collections.emptyList();
        }
        List<SearchResult> catResults = searchResultsByCategory.get(key);
        if (catResults == null) {
            return Collections.emptyList();
        }
        List<SearchResult> trimmedCatResults;
        if (catResults.size() > limit) {
            trimmedCatResults = catResults.subList(0, limit);
        }
        else {
            trimmedCatResults = catResults;
        }
        trimmedResult.addAll(trimmedCatResults);
        return trimmedResult;
    }

    private SearchCategoryGroupKey getKeyForCategory(String cat) {
        for (SearchCategoryGroupKey searchCategoryGroupKey : searchResultsByCategory.keySet()) {
            if (searchCategoryGroupKey.subcategory.equals(cat)) {
                return searchCategoryGroupKey;
            }
        }
        return null;
    }


    private void buildCatResults() {
        searchResultsByCategory = new HashMap<>();
        for (SearchResult searchResult : searchResults) {
            String subcategory = searchResult.getGroupDescription();
            SearchCategory category = searchResult.getCategory();
            SearchCategoryGroupKey key = new SearchCategoryGroupKey(category, subcategory);
            List<SearchResult> catResults = searchResultsByCategory.get(key);
            if (catResults == null) {
                catResults = new ArrayList<>();
                searchResultsByCategory.put(key, catResults);
            }
            catResults.add(searchResult);
        }
    }


    public List<SearchResult> getSearchResults() {
        return Collections.unmodifiableList(searchResults);
    }


    private class SearchCategoryGroupKey implements Comparable<SearchCategoryGroupKey> {

        private SearchCategory category;

        private String subcategory;

        private SearchCategoryGroupKey(SearchCategory category, String subcategory) {
            this.category = category;
            this.subcategory = subcategory;
        }

        public int compareTo(SearchCategoryGroupKey o) {
            int typeDiff = this.category.ordinal() - o.category.ordinal();
            if (typeDiff != 0) {
                return typeDiff;
            }
            return this.subcategory.compareToIgnoreCase(o.subcategory);
        }

        @Override
        public int hashCode() {
            return SearchCategoryGroupKey.class.getSimpleName().hashCode() + category.hashCode() + subcategory.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof SearchCategoryGroupKey)) {
                return false;
            }
            SearchCategoryGroupKey other = (SearchCategoryGroupKey) obj;
            return this.category == other.category && this.subcategory.equals(other.subcategory);
        }
    }
}
