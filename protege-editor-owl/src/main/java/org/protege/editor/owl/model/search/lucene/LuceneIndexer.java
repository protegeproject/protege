package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.lucene.analyzer.IriStringAnalyzer;
import org.protege.editor.owl.model.search.lucene.analyzer.TermStringAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 10/11/2015
 */
public class LuceneIndexer extends AbstractLuceneIndexer {

    @SuppressWarnings("serial")
    public LuceneIndexer() throws IOException {
        super(new PerFieldAnalyzerWrapper(new StandardAnalyzer(), new HashMap<String, Analyzer>() {{
            put(IndexField.ENTITY_IRI, new IriStringAnalyzer());
            put(IndexField.DISPLAY_NAME, new TermStringAnalyzer());
        }}));
    }

    @Override
    protected Directory setupIndexDirectory() {
        try {
            Path path = Files.createTempDirectory("protege-lucene-");
            logger.info("... storing index files in " + path);
            return FSDirectory.open(path);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
