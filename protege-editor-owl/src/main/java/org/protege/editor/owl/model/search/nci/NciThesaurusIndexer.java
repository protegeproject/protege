package org.protege.editor.owl.model.search.nci;

import org.protege.editor.owl.model.search.SearchContext;
import org.protege.editor.owl.model.search.lucene.AbstractLuceneIndexer;
import org.protege.editor.owl.model.search.lucene.IndexField;
import org.protege.editor.owl.model.search.lucene.analyzer.PhoneticAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/11/2015
 */
public class NciThesaurusIndexer extends AbstractLuceneIndexer {

    @SuppressWarnings("serial")
    public NciThesaurusIndexer() throws IOException {
        super(new PerFieldAnalyzerWrapper(new StandardAnalyzer(), new HashMap<String, Analyzer>() {{
            put(IndexField.PHONETIC_NAME, new PhoneticAnalyzer());
        }}));
    }

    @Override
    protected Directory setupIndexDirectory() {
        try {
            Path path = Files.createTempDirectory("nci-lucene-");
            logger.info("... storing index files in " + path);
            return FSDirectory.open(path);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Document createEntityDocument(OWLEntity entity, SearchContext context)
    {
       Document doc = super.createEntityDocument(entity, context);
       doc.add(new TextField(IndexField.PHONETIC_NAME, getDisplayName(entity, context), Store.NO));
       return doc;
    }

    @Override
    protected String getAnnotationText(OWLAnnotation annotation, SearchContext context) {
        OWLAnnotationValue value = annotation.getValue();
        if (value instanceof OWLLiteral) {
            OWLLiteral literal = (OWLLiteral) value;
            return String.format("\"%s\"", strip(literal.getLiteral()));
        }
        return String.format("\"%s\"", value.toString());
    }

    private String strip(String s) {
        return s.replaceAll("\\^\\^.*$", "") // remove datatype ending
                .replaceAll("^\"|\"$", "") // remove enclosed quotes
                .replaceAll("<[^>]+>", " ") // trim XML tags
                .replaceAll("\\s+", " ") // trim excessive white spaces
                .trim();
    }
}
