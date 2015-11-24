package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.SearchKeyword.Occurance;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class LuceneUtils {

    public static BooleanQuery createQuery(String searchField, String searchString, Analyzer analyzer) throws ParseException {
        QueryParser parser = new QueryParser(searchField, analyzer);
        Query query = parser.parse(searchString);
        if (!(query instanceof BooleanQuery)) {
            return new BooleanQuery.Builder().add(query, Occur.MUST).build();
        }
        return (BooleanQuery) query;
    }

    public static PhraseQuery createPhraseQuery(String searchField, String searchString) {
        return new PhraseQuery(searchField, searchString.split("\\s+"));
    }

    public static TermQuery createTermQuery(String searchField, String searchString) {
        return new TermQuery(new Term(searchField, searchString));
    }

    public static Occur toOccur(Occurance occurance) {
        switch (occurance) {
            case INCLUDE: return Occur.MUST;
            case EXCLUDE: return Occur.MUST_NOT;
            case OPTIONAL: return Occur.SHOULD;
            default: return Occur.SHOULD;
        }
    }
}
