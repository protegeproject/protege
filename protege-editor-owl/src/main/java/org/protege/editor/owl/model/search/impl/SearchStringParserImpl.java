package org.protege.editor.owl.model.search.impl;

import org.protege.editor.owl.model.find.OWLEntityFinderPreferences;
import org.protege.editor.owl.model.search.CompoundKeyword;
import org.protege.editor.owl.model.search.SearchInputHandler;
import org.protege.editor.owl.model.search.SearchKeyword;
import org.protege.editor.owl.model.search.SearchKeyword.Occurance;
import org.protege.editor.owl.model.search.SearchStringParser;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/11/2015
 */
public class SearchStringParserImpl implements SearchStringParser {

    private CompoundKeyword compoundKeyword = new CompoundKeyword();

    private SearchInputHandler searchInputHandler;

    @Override
    public void setSearchInputHandler(SearchInputHandler handler) {
        searchInputHandler = handler;
    }

    @Override
    public void parse(String searchString) {
        OWLEntityFinderPreferences prefs = OWLEntityFinderPreferences.getInstance();
        for (String splitSearchString : searchString.split("\\s+")) {
            SearchKeyword keyword = new SearchKeyword("",
                    splitSearchString,
                    Occurance.INCLUDE,
                    prefs.isCaseSensitive(),
                    prefs.isIgnoreWhiteSpace(),
                    prefs.isWholeWords(),
                    prefs.isUseRegularExpressions(),
                    false);
            compoundKeyword.add(keyword);
        }
        searchInputHandler.handle(compoundKeyword);
    }
}
