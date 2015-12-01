package org.protege.editor.owl.model.search.nci;

import org.protege.editor.owl.model.search.SearchInput;
import org.protege.editor.owl.model.search.SearchInputHandler;
import org.protege.editor.owl.model.search.SearchKeyword;
import org.protege.editor.owl.model.search.SearchKeyword.Occurance;
import org.protege.editor.owl.model.search.SearchStringParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 13/11/2015
 */
public class NciSearchStringParser implements SearchStringParser {

    private static final Pattern filteredSearchStringPattern = Pattern.compile("([^:,]*):(\"[^\"]*\"|[^,\"]*)");
    private static final Pattern nestedKeywordPattern = Pattern.compile("([^>]+)");

    private static final Pattern phoneticKeywordPattern = Pattern.compile("~(.*)");

    @Override
    public void parse(String searchString, SearchInputHandler handler) {
        OrSearch orSearch = new OrSearch();
        for (String searchGroupString : searchString.split("\\|")) { // divide per group
            SearchInput searchGroup = parseSearchGroup(searchGroupString.trim());
            orSearch.add(searchGroup);
        }
        handler.handle(orSearch);
    }
    
    public SearchInput parseSearchGroup(String searchGroupString) {
        if (searchGroupString.contains("&")) {
            return parseAndSearchString(searchGroupString);
        } else if (searchGroupString.contains(">")) {
            return parseNestedSearchString(searchGroupString);
        } else {
            return parseSearchString(searchGroupString);
        }
    }
    
    private AndSearch parseAndSearchString(String andSearchString) {
        AndSearch andSearch = new AndSearch();
        for (String keywordString : andSearchString.split("&")) {
            SearchKeyword keyword = parseSearchString(keywordString);
            andSearch.add(keyword);
        }
        return andSearch;
    }

    private NestedSearch parseNestedSearchString(String nestedSearchString) {
        NestedSearch nestedSearch = new NestedSearch();
        Matcher m = nestedKeywordPattern.matcher(nestedSearchString);
        while (m.find()) {
            String searchString = markSearchStringAsSearchField(m.group(1).trim());
            SearchKeyword keyword = parseSearchString(searchString);
            nestedSearch.add(keyword);
        }
        return nestedSearch;
    }

    private static String markSearchStringAsSearchField(String searchString) {
        return (searchString.contains(":")) ? searchString : searchString + ":";
    }

    private SearchKeyword parseSearchString(String searchString) {
        String searchField = "";
        Matcher m = filteredSearchStringPattern.matcher(searchString);
        if (m.find()) {
            searchField = m.group(1).trim();
            searchString = m.group(2).trim();
        }
        searchString = addDefaultIncludeSignToPlainKeyword(searchString);
        SearchKeyword keyword = getKeyword(searchField, searchString, true);
        return keyword;
    }

    private static String addDefaultIncludeSignToPlainKeyword(String searchString) {
        StringBuffer sb = new StringBuffer();
        for (String s : searchString.split(" ")) {
            if (s.isEmpty()) continue;
            char prefix = s.charAt(0);
            switch (prefix) {
                case '+': // include sign
                case '-': // exclude sign
                case '~': sb.append(s); break; // phonetic sign
                default: sb.append("+" + s);
            }
            sb.append(" ");
        }
        return sb.toString();
    }

    private SearchKeyword getKeyword(String searchField, String keywordSearch, boolean isFirstKeyword) {
        boolean searchByPhonetic = false;
        Matcher phoneticMatcher = phoneticKeywordPattern.matcher(keywordSearch);
        if (phoneticMatcher.find()) {
            searchByPhonetic = true;
            keywordSearch = phoneticMatcher.group(1);
        }
        SearchKeyword searchKeyword = createSearchKeyword(searchField, keywordSearch, searchByPhonetic);
        return searchKeyword;
    }

    private SearchKeyword createSearchKeyword(String field, String keyword, boolean searchByPhonetic) {
        return new SearchKeyword(field,
                keyword,
                Occurance.INCLUDE,
                false, // is case-sensitive
                false, // ignore whitespace
                false, // search whole words
                false, // search by regex
                searchByPhonetic);
    }

}
