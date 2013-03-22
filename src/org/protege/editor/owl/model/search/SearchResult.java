package org.protege.editor.owl.model.search;

import org.protege.editor.owl.ui.renderer.styledstring.StyledString;
import org.semanticweb.owlapi.model.OWLObject;

import java.util.regex.Pattern;

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

    private Pattern searchPattern;

    private int matchStart;

    private int matchEnd;

    public SearchResult(SearchMetadata searchMetadata, Pattern searchPattern, int matchStart, int matchEnd) {
        this.searchMetadata = searchMetadata;
        this.searchPattern = searchPattern;
        this.matchStart = matchStart;
        this.matchEnd = matchEnd;
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

    public Pattern getSearchPattern() {
        return searchPattern;
    }

    public int getMatchStart() {
        return matchStart;
    }

    public int getMatchEnd() {
        return matchEnd;
    }

    public int compareTo(SearchResult o) {
        int mdDiff = searchMetadata.compareTo(o.searchMetadata);
        if (mdDiff != 0) {
            return mdDiff;
        }
        int startDiff = this.matchStart - o.matchStart;
        if (startDiff != 0) {
            return startDiff;
        }
        return this.matchEnd - o.matchEnd;
    }

    @Override
    public int hashCode() {
        return SearchResult.class.getSimpleName().hashCode() + searchMetadata.hashCode() + matchStart + matchEnd;
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
        return this.searchMetadata.equals(other.searchMetadata) && this.matchStart == other.matchStart && this.matchEnd == other.matchEnd;
    }
}
