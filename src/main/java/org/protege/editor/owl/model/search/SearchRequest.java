package org.protege.editor.owl.model.search;

import java.util.regex.Pattern;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 19/09/2012
 * <p>
 * Captures a text based search request
 * </p>
 */
public class SearchRequest {

    private Pattern searchPattern;

    /**
     * Constructs a search request from a regular expression pattern.
     * @param searchPattern The pattern to match against.
     */
    public SearchRequest(Pattern searchPattern) {
        this.searchPattern = searchPattern;
    }

    /**
     * Gets the search request as a regular expression pattern.
     * @return The search request as a regular expression pattern.
     */
    public Pattern getSearchPattern() {
        return searchPattern;
    }

    @Override
    public int hashCode() {
        return SearchRequest.class.getSimpleName().hashCode() + this.searchPattern.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SearchRequest)) {
            return false;
        }
        SearchRequest other = (SearchRequest) obj;
        return this.searchPattern.equals(other.searchPattern);
    }
}
