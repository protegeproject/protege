package org.protege.editor.owl.model.search;

import org.protege.editor.owl.model.search.impl.SearchMetadata;
import org.protege.editor.owl.ui.renderer.styledstring.StyledString;

import org.semanticweb.owlapi.model.OWLObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

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

    private SearchMetadata metadata;

    private final ImmutableList<SearchResultMatch> matches;

    public SearchResult(SearchMetadata metadata, ImmutableList<SearchResultMatch> matches) {
        this.metadata = metadata;
        this.matches = matches;
    }

    public SearchCategory getCategory() {
        return metadata.getCategory();
    }

    public String getGroupDescription() {
        return metadata.getGroupDescription();
    }

    public String getSearchString() {
        return metadata.getSearchString();
    }

    public StyledString getStyledSearchSearchString() {
        return metadata.getStyledSearchSearchString();
    }

    public OWLObject getSubject() {
        return metadata.getSubject();
    }

    public String getSubjectRendering() {
        return metadata.getSubjectRendering();
    }

    public SearchMetadata getSearchMetadata() {
        return metadata;
    }

    public ImmutableList<SearchResultMatch> getMatches() {
        return matches;
    }

    public int compareTo(SearchResult o) {
        int matchesDiff = Ordering.<SearchResultMatch>natural().lexicographical().compare(this.matches, o.matches);
        if(matchesDiff != 0) {
            return matchesDiff;
        }
        return metadata.compareTo(o.metadata);
    }

    @Override
    public int hashCode() {
        return SearchResult.class.getSimpleName().hashCode() + metadata.hashCode() + matches.hashCode();
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
        return this.metadata.equals(other.metadata) && this.matches.equals(other.matches);
    }

    @Override
    public String toString() {
        return String.format("(%s,%s,%s)", getCategory().name(), getGroupDescription(), getSubjectRendering());
    }
}
