package org.protege.editor.owl.model.search;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import org.protege.editor.owl.ui.renderer.styledstring.StyledString;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 18/09/2012
 * <p>
 * Represents the result of a search.
 * </p>
 */
public class SearchResult implements Comparable<SearchResult> {

    private SearchMetadata searchMetadata;

    private final ImmutableList<SearchResultMatch> matches;

    public SearchResult(SearchMetadata searchMetadata, ImmutableList<SearchResultMatch> matches) {
        this.searchMetadata = searchMetadata;
        this.matches = matches;
    }

    public SearchCategory getCategory() {
        return searchMetadata.getCategory();
    }

    public String getGroupDescription() {
        return searchMetadata.getGroupDescription();
    }

    public String getSearchString() {
        return searchMetadata.getSearchString();
    }

    public StyledString getStyledSearchSearchString() {
        return searchMetadata.getStyledSearchSearchString();
    }

    public OWLObject getSubject() {
        return searchMetadata.getSubject();
    }

    public String getSubjectRendering() {
        return searchMetadata.getSubjectRendering();
    }

    public ImmutableList<SearchResultMatch> getMatches() {
        return matches;
    }

    public int compareTo(SearchResult o) {
        int matchesDiff = Ordering.<SearchResultMatch>natural().lexicographical().compare(this.matches, o.matches);
        if(matchesDiff != 0) {
            return matchesDiff;
        }
        return searchMetadata.compareTo(o.searchMetadata);
    }

    @Override
    public int hashCode() {
        return SearchResult.class.getSimpleName().hashCode() + searchMetadata.hashCode() + matches.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SearchResult)) {
            return false;
        }
        SearchResult other = (SearchResult) obj;
        return this.searchMetadata.equals(other.searchMetadata) && this.matches.equals(other.matches);
    }
}
