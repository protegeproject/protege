package org.protege.editor.owl.model.search.nci;

import org.protege.editor.owl.model.search.SearchCategory;
import org.protege.editor.owl.model.search.SearchKeyword;
import org.protege.editor.owl.model.search.lucene.IndexField;
import org.protege.editor.owl.model.search.lucene.LuceneSearcher;
import org.protege.editor.owl.model.search.lucene.SearchQuery;
import org.protege.editor.owl.model.search.lucene.SearchQueryBuilder;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 19/11/2015
 */
public class TraversableQuery extends SearchQuery {

    private List<SearchKeyword> hangingKeywords = new ArrayList<>();

    public TraversableQuery(BooleanQuery query, List<SearchKeyword> hangingKeywords, LuceneSearcher searcher) {
        super(query, SearchCategory.ANNOTATION_VALUE, searcher);
        this.hangingKeywords.addAll(hangingKeywords);
    }

    @Override
    public Set<Document> evaluate() throws IOException {
        Set<Document> toReturn = new HashSet<>();
        toReturn.addAll(evaluateBackTrack(this));
        for (SearchKeyword hangingKeyword : hangingKeywords) {
            Set<Document> docs = new HashSet<>();
            swap(toReturn, docs);
            for (Document doc : docs) {
                SearchQueryBuilder builder = new NciQueryForFilteredAnnotationBuilder(searcher);
                builder.add(new SearchKeywordWrapper(hangingKeyword, prepareKeyword(doc)));
                SearchQuery searchQuery = builder.build();
                toReturn.addAll(evaluateBackTrack(searchQuery));
            }
        }
        return toReturn;
    }

    private static String prepareKeyword(Document doc) {
        String iriString = doc.get(IndexField.ENTITY_IRI);
//        int pos = iriString.lastIndexOf('#');
//        String localName = (pos > 0) ? iriString.substring(pos+1, iriString.length()) : iriString;
        return String.format("+\"%s\"", iriString);
    }

    private Set<Document> evaluateBackTrack(SearchQuery searchQuery) throws IOException {
        Set<Document> docs = new HashSet<>();
        TopDocs hits = searcher.search(searchQuery.getQuery());
        for (int i = 1; i <= hits.scoreDocs.length; i++) {
            Document doc = searcher.find(hits.scoreDocs[i-1].doc);
            docs.add(doc);
        }
        return docs;
    }

    private void swap(Set<Document> toReturn, Set<Document> docs) {
        docs.addAll(toReturn);
        toReturn.clear();
    }

    class SearchKeywordWrapper extends SearchKeyword {
        public SearchKeywordWrapper(SearchKeyword keyword, String keywordString) {
            super(keyword.getField(),
                    keywordString,
                    keyword.occurance(),
                    keyword.isCaseSensitive(),
                    keyword.isIgnoreWhitespace(),
                    keyword.searchWholeWords(),
                    keyword.searchByRegex(),
                    keyword.searchByPhonetic());
        }
    }
}
