package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.SearchContext;
import org.protege.editor.owl.model.search.SearchIndexPreferences;
import org.protege.editor.owl.model.search.lucene.AbstractLuceneIndexer.IndexProgressListener;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 29/01/2016
 */
public class IndexDelegator {

    private static final Logger logger = LoggerFactory.getLogger(IndexDelegator.class);

    public static final String PREFIX_LABEL = "ProtegeIndex";

    private String currentOntologyVersion = "";

    private String previousOntologyVersion = "";

    private IndexDirectory indexDirectory;

    private OWLOntology ontology;

    private boolean isIndexReady = false;

    private final SearchIndexPreferences preferences = SearchIndexPreferences.getInstance();

    public IndexDelegator(OWLOntology ontology) {
        this.ontology = ontology;
        String ontologyVersion = getOntologyVersion(ontology);
        Optional<String> indexLocation = preferences.getIndexLocation(ontologyVersion);
        if (indexLocation.isPresent()) {
            String existingIndexLocation = indexLocation.get();
            indexDirectory = new IndexDirectory(existingIndexLocation);
            logger.info("Loading index files from " + indexDirectory.getLocation());
            isIndexReady = true;
        } else {
            String directoryName = buildIndexDirectoryName(ontology);
            String newIndexLocation = preferences.createIndexLocation(directoryName);
            indexDirectory = new IndexDirectory(newIndexLocation);
            preferences.registerIndexLocation(ontologyVersion, newIndexLocation);
            isIndexReady = false;
        }
        updateOntologyVersion();
    }

    public OWLOntology getActiveOntology() {
        return ontology;
    }

    public String getActiveOntologyVersion() {
        return currentOntologyVersion;
    }

    protected String getPreviousOntologyVersion() {
        return previousOntologyVersion;
    }

    public Directory getIndexDirectory() throws IOException {
        return indexDirectory.getPhysicalDirectory();
    }

    public void doIndex(AbstractLuceneIndexer indexer, SearchContext context, IndexProgressListener listener) throws IOException {
        if (!isIndexReady) {
            long t0 = System.currentTimeMillis();
            logger.info("Building search index...");
            
            final IndexWriter writer = getIndexWriter(indexer.getIndexWriterConfig());
            for (OWLOntology ontology : context.getOntologies()) {
                logger.info("... building index for " + ontology.getOntologyID());
                
                // Calculate first the total number of axioms that will get indexed
                int totalAxiomCount = ontology.getAxiomCount(AxiomType.DECLARATION) + ontology.getAxiomCount(AxiomType.ANNOTATION_ASSERTION);
                if (totalAxiomCount == 0) continue; // skip if zero
                
                // Start indexing
                int progress = 1;
                for (OWLEntity entity : ontology.getSignature(Imports.INCLUDED)) {
                    writer.addDocument(indexer.createEntityDocument(entity, context));
                    listener.fireIndexingProgressed(percentage(progress++, totalAxiomCount));
                    for (OWLAnnotation annotation : EntitySearcher.getAnnotations(entity, ontology)) {
                        writer.addDocument(indexer.createAnnotationDocument(entity, annotation, context));
                        listener.fireIndexingProgressed(percentage(progress++, totalAxiomCount));
                    }
                }
            }
            writer.close();
            isIndexReady = true;

            long t1 = System.currentTimeMillis();
            logger.info("... built search index in " + (t1 - t0) + " ms");
        }
    }

    public boolean doUpdate(AbstractLuceneIndexer indexer, ChangeSet changeSet, SearchContext context) throws IOException {
        if (isIndexReady) {
            logger.info("... updating index from {} change(s)", changeSet.size());
            
            final IndexWriter writer = getIndexWriter(indexer.getIndexWriterConfig());

            // Process the remove change set first ...
            for (OWLEntity entity : changeSet.getRemoveDeclarations()) {
                writer.deleteDocuments(new Term(IndexField.ENTITY_IRI, indexer.getEntityId(entity)));
            }
            for (Entry<OWLEntity, OWLAnnotation> annotationEntry : changeSet.getRemoveAnnotations()) {
                writer.deleteDocuments(
                        new Term(IndexField.ENTITY_IRI, indexer.getEntityId(annotationEntry.getKey())),
                        new Term(IndexField.ANNOTATION_IRI, indexer.getAnnotationId(annotationEntry.getValue())));
            }
            // ... and then continue process the addition change set.
            for (OWLEntity entity : changeSet.getAddDeclarations()) {
                writer.addDocument(indexer.createEntityDocument(entity, context));
            }
            for (Entry<OWLEntity, OWLAnnotation> annotationEntry : changeSet.getAddAnnotations()) {
                writer.addDocument(indexer.createAnnotationDocument(annotationEntry.getKey(), annotationEntry.getValue(), context));
            }
            writer.close();
            return true;
        }
        return false;
    }

    public void saveVersion() {
        if (updateOntologyVersion()) {
            preferences.clearIndexLocation(getPreviousOntologyVersion());
            preferences.registerIndexLocation(getActiveOntologyVersion(), indexDirectory.getLocation());
            logger.info("Saved index IndexVersion({}) to {}", getPreviousOntologyVersion(), indexDirectory.getLocation());
        } else {
            logger.info("No ontology changes detected. Not writing new index version.");
        }
    }

    /*
     * Private utility methods
     */

    private IndexWriter getIndexWriter(IndexWriterConfig indexWriterConfig) throws IOException {
        return new IndexWriter(indexDirectory.getPhysicalDirectory(), indexWriterConfig);
    }

    private String buildIndexDirectoryName(OWLOntology ontology) {
        String ontologyIdHex = Integer.toHexString(ontology.getOntologyID().toString().hashCode());
        String timestampHex = Integer.toHexString(new Date().hashCode());
        return String.format("%s-%s-%s", PREFIX_LABEL, ontologyIdHex, timestampHex);
    }

    private String getOntologyVersion(OWLOntology ontology) {
        final int prime = 31;
        int result = 1;
        result = prime * result + ontology.getSignature().hashCode();
        result = prime * result + ontology.getAnnotations().hashCode();
        return Integer.toHexString(result);
    }

    private boolean updateOntologyVersion() {
        String newVersion = getOntologyVersion(getActiveOntology());
        if (!currentOntologyVersion.equals(newVersion)) {
            previousOntologyVersion = currentOntologyVersion;
            currentOntologyVersion = newVersion;
            return true;
        }
        return false;
    }

    private int percentage(int progress, int total) {
        return (progress * 100) / total;
    }
}
