package org.protege.editor.owl.model.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Josef Hardi <johardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 22/06/2016
 */
public class SearchInput implements Iterable<SearchKeyword> {

    private List<SearchKeyword> keywords;

    private SearchInput(List<SearchKeyword> keywords) {
        this.keywords = keywords;
    }

    public boolean isEmpty() {
        return keywords.isEmpty();
    }

    public int size() {
        return keywords.size();
    }

    @Override
    public final Iterator<SearchKeyword> iterator() {
        return keywords.iterator();
    }

    public void accept(SearchInputVisitor visitor) {
        visitor.visit(this);
    }

    public static class Builder {

        private final List<SearchKeyword> keywords = new ArrayList<>();

        public Builder() {
            // NO-OP
        }

        public Builder add(SearchKeyword keyword) {
            keywords.add(keyword);
            return this;
        }

        public SearchInput build() {
            return new SearchInput(keywords);
        }
    }
}
