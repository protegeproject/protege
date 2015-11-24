package org.protege.editor.owl.model.search.nci;

import org.protege.editor.owl.model.search.SearchCategory;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.model.search.SearchResultMatch;
import org.protege.editor.owl.ui.renderer.styledstring.StyledString;

import com.google.common.collect.Ordering;

public class EntityOnlySearchResult extends SearchResult {

    private static final SearchCategory DEFAULT_SEARCH_CATEGORY = SearchCategory.DISPLAY_NAME;
    private static final String DEFAULT_GROUP_DESCRIPTION = "SUBJECT ENTITY";
    private static final String DEFAULT_SEARCH_STRING = "(Found in multiple fields)";

    public EntityOnlySearchResult(SearchResult searchResult) {
        super(searchResult.getResultItem(), searchResult.getMatches());
    }

    @Override
    public SearchCategory getCategory() {
        return DEFAULT_SEARCH_CATEGORY;
    }

    @Override
    public String getGroupDescription() {
        return DEFAULT_GROUP_DESCRIPTION;
    }

    @Override
    public String getSearchString() {
        return DEFAULT_SEARCH_STRING;
    }

    @Override
    public StyledString getStyledSearchSearchString() {
        return new StyledString(DEFAULT_SEARCH_STRING);
    }

    public int compareTo(EntityOnlySearchResult o) {
        int matchesDiff = Ordering.<SearchResultMatch>natural().lexicographical().compare(this.getMatches(), o.getMatches());
        if(matchesDiff != 0) {
            return matchesDiff;
        }
        return getSubject().compareTo(o.getSubject());
    }

    @Override
    public int hashCode() {
        return SearchResult.class.getSimpleName().hashCode() + getCategory().hashCode()
                + getGroupDescription().hashCode() + getSubject().hashCode();
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
        return this.getCategory().equals(other.getCategory())
                && this.getGroupDescription().equals(other.getGroupDescription())
                && this.getSubject().equals(other.getSubject());
    }

    @Override
    public String toString() {
        return String.format("(%s,%s,%s)", getCategory().name(), getGroupDescription(), getSubjectRendering());
    }
}
