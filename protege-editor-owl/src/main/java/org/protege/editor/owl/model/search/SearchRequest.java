package org.protege.editor.owl.model.search;


import com.google.common.collect.ImmutableList;

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

    private ImmutableList<Pattern> searchPatterns;

    /**
     * Constructs a search request from a regular expression pattern.
     * @param searchPatterns A list of search patterns.  All patterns must be matched for the search to succeed.
     */
    public SearchRequest(ImmutableList<Pattern> searchPatterns) {
        this.searchPatterns = searchPatterns;
    }

    /**
     * Gets the search request as a regular expression pattern list.
     * @return The search request as a regular expression pattern list.
     */
    public ImmutableList<Pattern> getSearchPatterns() {
        return searchPatterns;
    }

    @Override
    public int hashCode() {
        return SearchRequest.class.getSimpleName().hashCode() + this.searchPatterns.hashCode();
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
        return this.searchPatterns.equals(other.searchPatterns);
    }
}
