package org.protege.editor.owl.model.search.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.pattern.PatternTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;

import java.util.regex.Pattern;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/11/2015
 */
public class IriStringAnalyzer extends Analyzer {

    public static final int DEFAULT_MIN_GRAM_SIZE = 3;
    public static final int DEFAULT_MAX_GRAM_SIZE = Integer.MAX_VALUE;

    private int minGramSize = DEFAULT_MIN_GRAM_SIZE;
    private int maxGramSize = DEFAULT_MAX_GRAM_SIZE;

    private final Pattern localNameFragmentPattern = Pattern.compile("[/|#]([\\w\\-]+)$");

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final PatternTokenizer src = new PatternTokenizer(localNameFragmentPattern, 1);
        
        TokenStream tok = new StandardFilter(src);
        tok = new LowerCaseFilter(tok); // lowercased only
        tok = new NGramTokenFilter(tok, minGramSize, maxGramSize); // create word n-grams
        return new TokenStreamComponents(src, tok);
    }
}
