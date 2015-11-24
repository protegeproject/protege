package org.protege.editor.owl.model.search.impl;

import org.protege.editor.owl.model.search.CompoundKeyword;
import org.protege.editor.owl.model.search.SearchInputHandlerBase;
import org.protege.editor.owl.model.search.SearchKeyword;

import java.util.List;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/11/2015
 */
public class PatternBasedInputHandler extends SearchInputHandlerBase {

    private ImmutableList.Builder<Pattern> builder = ImmutableList.builder();

    public List<Pattern> getSearchPattern() {
        return builder.build();
    }

    @Override
    public void handle(SearchKeyword searchKeyword) {
        int flags = Pattern.DOTALL | (searchKeyword.isCaseSensitive() ? 0 : Pattern.CASE_INSENSITIVE);
        String keywordString = searchKeyword.getString();
        if (searchKeyword.searchWholeWords()) {
            keywordString = "\\b(:?" + keywordString + ")\\b";
        }
        builder.add(Pattern.compile(keywordString, flags));
    }

    @Override
    public void handle(CompoundKeyword compoundKeyword) {
        for (SearchKeyword keyword : compoundKeyword) {
            handle(keyword);
        }
    }
}
