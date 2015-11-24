package org.protege.editor.owl.model.search;

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

    private Object searchObject;

    /**
     * Constructs a search request from a search input.
     */
    public SearchRequest(Object searchObject) {
        this.searchObject = searchObject;
    }

    public Object getSearchObject() {
        return searchObject;
    }

    @Override
    public int hashCode() {
        return SearchRequest.class.getSimpleName().hashCode() + this.searchObject.hashCode();
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
        return this.searchObject.equals(other.searchObject);
    }
}
