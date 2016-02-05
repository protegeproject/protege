package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.lucene.AbstractLuceneIndexer.IndexProgressListener;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 29/01/2016
 */
public class IndexDirectory {

    private String indexLocation;

    private boolean createNew = false;

    // Prevent external instantiation
    private IndexDirectory(String indexLocation, boolean createNew) {
        this.indexLocation = indexLocation;
        this.createNew = createNew;
    }

    /**
     * Create a new <code>IndexDirectory</code> given the full path location.
     *
     * @param indexLocation
     *          A valid full path directory location.
     * @return An <code>IndexDirectory</code> object.
     */
    public static IndexDirectory create(String indexLocation) {
        return new IndexDirectory(indexLocation, true);
    }

    /**
     * Create a new <code>IndexDirectory</code> by loading an existing directory location.
     *
     * @param indexLocation
     *          A valid full path directory location.
     * @return An <code>IndexDirectory</code> object.
     */
    public static IndexDirectory load(String indexLocation) {
        return new IndexDirectory(indexLocation, false);
    }

    /**
     * Get the {@link Directory} object from this index directory instance.
     *
     * @return Returns a {@link Directory} object.
     * @throws IOException
     */
    public Directory getPhysicalDirectory() throws IOException {
        return FSDirectory.open(Paths.get(indexLocation));
    }

    /**
     * Get the string location of the index directory full path location.
     *
     * @return Returns a directory location.
     */
    public String getLocation() {
        return indexLocation;
    }

    /**
     * Start the indexing process given a set of documents to index. Additionally an
     * {@link IndexProgressListener} can be added to catch the indexing progress.
     *
     * @param indexWriter
     *          A writer to writer the index.
     * @param documents
     *          A set of Lucene {@link Document} to index.
     * @param listener
     *          An optional listener to monitor the indexing progress.
     * @throws IOException
     */
    public void doIndex(IndexWriter indexWriter, Set<Document> documents, IndexProgressListener listener) throws IOException {
        int progress = 1;
        for (Document doc : documents) {
            doAdd(indexWriter, doc);
            if (listener != null) {
                listener.fireIndexingProgressed(percentage(progress++, documents.size()));
            }
        }
        createNew = false;
    }

    /**
     * Delete an index item from the index tree given one or more terms to select.
     *
     * @param indexWriter
     *          A writer to delete the index item.
     * @param terms
     *          One or more terms to select the deleted index item.
     * @throws IOException
     */
    public void doDelete(IndexWriter indexWriter, final Term... terms) throws IOException {
        indexWriter.deleteDocuments(terms);
    }

    /**
     * Add an index item to the index tree given a Lucene {@link Document} to put in.
     * 
     * @param indexWriter
     *          A writer to write the index item.
     * @param document
     *          A Lucene {@link Document} to put in.
     * @throws IOException
     */
    public void doAdd(IndexWriter indexWriter, final Document document) throws IOException {
        indexWriter.addDocument(document);
    }

    /**
     * Check if the directory contains the index tree. Or the indexing process was already initiated
     *
     * @return Returns <code>true</code> if the index is already available and ready to be used or
     * updated, and returns <code>false</code> otherwise.
     */
    public boolean isIndexAvailable() {
        return createNew ? false : true;
    }

    private int percentage(int progress, int total) {
        return (progress * 100) / total;
    }
}