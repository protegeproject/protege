package org.protege.editor.owl.model.search;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 24/09/2014
 */
public class SearchResultMatch implements Comparable<SearchResultMatch> {

    private String matchString;

    private int start;

    private int offset;

    public SearchResultMatch(String matchString, int start, int offset) {
        this.matchString = checkNotNull(matchString);
        this.start = start;
        this.offset = offset;
    }

    public String getMatchString() {
        return matchString;
    }

    public int getStart() {
        return start;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public int compareTo(SearchResultMatch other) {
        final int startDiff = this.start - other.start;
        if(startDiff != 0) {
            return startDiff;
        }
        final int endDiff = this.offset - other.offset;
        if(endDiff != 0) {
            return endDiff;
        }
        return matchString.compareTo(other.matchString);
    }

    @Override
    public int hashCode() {
        return "SearchResultMatch".hashCode()
                + matchString.hashCode()
                + start
                + offset;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(!(o instanceof SearchResultMatch)) {
            return false;
        }
        SearchResultMatch other = (SearchResultMatch) o;
        return this.matchString.equals(other.matchString)
                && this.start == other.start
                && this.offset == other.offset;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append("match: ").append(matchString).append(", ")
                 .append("startIndex: ").append(start).append(", ")
                 .append("offset: ").append(offset)
                 .toString();
    }
}
