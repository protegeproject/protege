package org.protege.editor.owl.model.search.impl;

import org.protege.editor.owl.model.search.SearchInput;
import org.protege.editor.owl.model.search.SearchInputVisitor;
import org.protege.editor.owl.model.search.SearchTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Josef Hardi <johardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 22/06/2016
 */
public class RegexPatternQueryBuilder implements SearchInputVisitor {

    private final List<Pattern> patterns = new ArrayList<>();

    @Override
    public void visit(SearchInput searchInput) {
        for (SearchTerm searchString : searchInput) {
            int flags = Pattern.DOTALL | (searchString.isCaseSensitive() ? 0 : Pattern.CASE_INSENSITIVE);
            String keywordString = searchString.getString();
            if (searchString.searchWholeWords()) {
                keywordString = "\\b(:?" + keywordString + ")\\b";
            }
            patterns.add(Pattern.compile(keywordString, flags));
        }
    }

    public List<Pattern> build() {
        return patterns;
    }
}
