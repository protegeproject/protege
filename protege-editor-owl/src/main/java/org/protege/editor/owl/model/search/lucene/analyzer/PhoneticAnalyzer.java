package org.protege.editor.owl.model.search.lucene.analyzer;

import org.apache.commons.codec.Encoder;
import org.apache.commons.codec.language.RefinedSoundex;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.phonetic.PhoneticFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/11/2015
 */
public class PhoneticAnalyzer extends Analyzer {

    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
    public static final int DEFAULT_MIN_GRAM_SIZE = 7 ;
    public static final int DEFAULT_MAX_GRAM_SIZE = Integer.MAX_VALUE;
    
    public static final Encoder DEFAULT_PHONETIC_ENCODER = new RefinedSoundex();

    private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;
    private int minGramSize = DEFAULT_MIN_GRAM_SIZE;
    private int maxGramSize = DEFAULT_MAX_GRAM_SIZE;
    
    private Encoder encoder = DEFAULT_PHONETIC_ENCODER;

    private CharArraySet stopWordsSet = StopAnalyzer.ENGLISH_STOP_WORDS_SET;

    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final StandardTokenizer src = new StandardTokenizer();
        src.setMaxTokenLength(maxTokenLength);
        
        TokenStream tok = new StandardFilter(src);
        tok = new LowerCaseFilter(tok); // lowercased only
        tok = new StopFilter(tok, stopWordsSet); // remove stopwords
        tok = new PhoneticFilter(tok, encoder, true); // store phonetic code
        tok = new NGramTokenFilter(tok, minGramSize, maxGramSize); // create word n-grams
        return new TokenStreamComponents(src, tok);
    }
}