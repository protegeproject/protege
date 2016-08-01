package org.protege.editor.owl.model.search.impl;

import org.protege.editor.owl.model.find.OWLEntityFinderPreferences;
import org.protege.editor.owl.model.search.SearchInput;
import org.protege.editor.owl.model.search.SearchTerm;
import org.protege.editor.owl.model.search.SearchStringParser;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/11/2015
 */
public class SearchStringParserImpl implements SearchStringParser {

    @Override
    public SearchInput parse(String searchString) {
        SearchInput.Builder builder = new SearchInput.Builder();
        OWLEntityFinderPreferences prefs = OWLEntityFinderPreferences.getInstance();
        for (String splitSearchString : searchString.split("\\s+")) {
            SearchTerm keyword = new SearchTerm("",
                    splitSearchString,
                    splitSearchString,
                    prefs.isCaseSensitive(),
                    prefs.isIgnoreWhiteSpace(),
                    prefs.isWholeWords(),
                    prefs.isUseRegularExpressions(),
                    false);
            builder.add(keyword);
        }
        return builder.build();
    }
}
