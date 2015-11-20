package org.protege.editor.owl.model.search;

import com.google.common.base.MoreObjects;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 24/09/2014
 */
public class SearchResultMatch implements Comparable<SearchResultMatch> {

    private Pattern pattern;

    private int start;

    private int end;

    public SearchResultMatch(Pattern pattern, int start, int end) {
        this.pattern = checkNotNull(pattern);
        this.start = start;
        this.end = end;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public int compareTo(SearchResultMatch other) {
        final int startDiff = this.start - other.start;
        if(startDiff != 0) {
            return startDiff;
        }
        final int endDiff = this.end - other.end;
        if(endDiff != 0) {
            return endDiff;
        }
        return pattern.pattern().compareTo(other.pattern.pattern());
    }

    @Override
    public int hashCode() {
        return "SearchResultMatch".hashCode()
                + pattern.hashCode()
                + start
                + end;
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
        return this.pattern.equals(other.pattern)
                && this.start == other.start
                && this.end == other.end;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("SearchResultMatch")
                      .addValue(pattern)
                      .add("start", start)
                      .add("end", end)
                      .toString();
    }
}
