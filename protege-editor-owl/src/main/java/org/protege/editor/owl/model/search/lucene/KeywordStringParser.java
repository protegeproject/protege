package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.CompoundKeyword;
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
 * Date: 04/11/2015
 */
public class KeywordStringParser implements SearchStringParser {

    private static final Pattern filteredSearchStringPattern = Pattern.compile("([^:]*):(.*)");
    private static final Pattern searchKeywordStringsPattern = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");

    private static final Pattern includeKeywordPattern = Pattern.compile("\\+([^\\s^\\\"^\\']+)");
    private static final Pattern excludeKeywordPattern = Pattern.compile("-([^\\s^\\\"^\\']+)");
    private static final Pattern wholeWordKeywordPattern = Pattern.compile("\"(.*)\"");

    @Override
    public void parse(String searchString, SearchInputHandler handler) {
        CompoundKeyword compoundKeyword = new CompoundKeyword();
        parseSearchString(compoundKeyword, searchString.trim());
        handler.handle(compoundKeyword);
    }

    public void parseSearchString(CompoundKeyword compoundKeyword, String searchString) {
        String searchField = "";
        Matcher m = filteredSearchStringPattern.matcher(searchString);
        if (m.find()) {
            searchField = m.group(1).trim();
            searchString = m.group(2).trim();
        }
        searchString = addDefaultIncludeSignToPlainKeyword(searchString);
        parseSearchKeyword(compoundKeyword, searchField, searchString);
    }

    private static String addDefaultIncludeSignToPlainKeyword(String searchString) {
        StringBuffer sb = new StringBuffer();
        for (String s : searchString.split(" ")) {
            if (s.isEmpty()) continue;
            char prefix = s.charAt(0);
            switch (prefix) {
                case '+': // include sign
                case '-': sb.append(s); break; // exclude sign
                default: sb.append("+" + s);
            }
            sb.append(" ");
        }
        return sb.toString();
    }

    public void parseSearchKeyword(CompoundKeyword compoundKeyword, String searchField, String searchString) {
        Matcher m = searchKeywordStringsPattern.matcher(searchString);
        while (m.find()) {
            String keywordString = m.group().trim();
            SearchKeyword keyword = getKeyword(searchField, keywordString);
            compoundKeyword.add(keyword);
        }
    }

    private SearchKeyword getKeyword(String searchField, String keywordSearch) {
        Occurance occurance = Occurance.INCLUDE;
        boolean searchWholeWord = false;
        
        Matcher includeMatcher = includeKeywordPattern.matcher(keywordSearch);
        if (includeMatcher.find()) {
            occurance = Occurance.INCLUDE;
            keywordSearch = includeMatcher.group(1);
        }
        
        Matcher excludeMatcher = excludeKeywordPattern.matcher(keywordSearch);
        if (excludeMatcher.find()) {
            occurance = Occurance.EXCLUDE;
            keywordSearch = excludeMatcher.group(1);
        }
        
        Matcher wholeWordMatcher = wholeWordKeywordPattern.matcher(keywordSearch);
        if (wholeWordMatcher.find()) {
            searchWholeWord = true;
            keywordSearch = wholeWordMatcher.group(1); 
        }
        SearchKeyword searchKeyword = createSearchKeyword(searchField, keywordSearch, occurance, searchWholeWord);
        return searchKeyword;
    }

    private SearchKeyword createSearchKeyword(String field, String keyword, Occurance occurance, boolean searchWholeWords) {
        return new SearchKeyword(field,
                keyword,
                occurance,
                false, // is case-sensitive
                false, // ignore whitespace
                searchWholeWords,
                false, // search by regex
                false); // search by phonetic
    }
}
