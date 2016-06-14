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

    private ResultItem resultItem;

    private final ImmutableList<SearchResultMatch> matches;

    public SearchResult(ResultItem resultItem, ImmutableList<SearchResultMatch> matches) {
        this.resultItem = resultItem;
        this.matches = matches;
    }

    public SearchCategory getCategory() {
        return resultItem.getCategory();
    }

    public String getGroupDescription() {
        return resultItem.getGroupDescription();
    }

    public String getSearchString() {
        return resultItem.getSearchString();
    }

    public StyledString getStyledSearchSearchString() {
        return resultItem.getStyledSearchSearchString();
    }

    public OWLObject getSubject() {
        return resultItem.getSubject();
    }

    public String getSubjectRendering() {
        return resultItem.getSubjectRendering();
    }

    public ResultItem getResultItem() {
        return resultItem;
    }

    public ImmutableList<SearchResultMatch> getMatches() {
        return matches;
    }

    public int compareTo(SearchResult o) {
        int matchesDiff = Ordering.<SearchResultMatch>natural().lexicographical().compare(this.matches, o.matches);
        if(matchesDiff != 0) {
            return matchesDiff;
        }
        return resultItem.compareTo(o.resultItem);
    }

    @Override
    public int hashCode() {
        return SearchResult.class.getSimpleName().hashCode() + resultItem.hashCode() + matches.hashCode();
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
        return this.resultItem.equals(other.resultItem) && this.matches.equals(other.matches);
    }

    @Override
    public String toString() {
        return String.format("(%s,%s,%s)", getCategory().name(), getGroupDescription(), getSubjectRendering());
    }
}
