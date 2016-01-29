package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.SearchIndexPreferences;

import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
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

    private final SearchIndexPreferences preferences = SearchIndexPreferences.getInstance();

    public IndexDelegator(OWLOntology ontology) {
        this.ontology = ontology;
        String ontologyVersion = computeOntologyVersion(ontology);
        Optional<String> indexLocation = preferences.getIndexLocation(ontologyVersion);
        if (indexLocation.isPresent()) {
            indexDirectory = IndexDirectory.load(indexLocation.get());
            logger.info("... reuse index files found in " + indexDirectory.getLocation());
        } else {
            String newIndexLocation = preferences.getBaseDirectory() + createDirectoryName(ontology);
            preferences.registerIndexLocation(ontologyVersion, newIndexLocation);
            indexDirectory = IndexDirectory.create(newIndexLocation);
            logger.info("... storing index files in " + indexDirectory.getLocation());
        }
    }

    public String getOntologyVersion() {
        return currentOntologyVersion;
    }

    public String getPreviousOntologyVersion() {
        return previousOntologyVersion;
    }

    public IndexDirectory getIndexDirectory() {
        return indexDirectory;
    }

    public void save() {
        computeOntologyVersion(ontology);
        preferences.registerIndexLocation(getPreviousOntologyVersion(), "");
        preferences.registerIndexLocation(getOntologyVersion(), indexDirectory.getLocation());
    }

    private String createDirectoryName(OWLOntology ontology) {
        String ontologyIdHex = Integer.toHexString(ontology.getOntologyID().toString().hashCode());
        String timestampHex = Integer.toHexString(new Date().hashCode());
        return String.format("%s-%s-%s", PREFIX_LABEL, ontologyIdHex, timestampHex);
    }

    private String computeOntologyVersion(OWLOntology ontology) {
        String ontologyVersion = hash(ontology);
        if (!ontologyVersion.equals(currentOntologyVersion)) {
            previousOntologyVersion = currentOntologyVersion;
            currentOntologyVersion = ontologyVersion;
        }
        return ontologyVersion;
    }

    private static String hash(Object o) {
        return Integer.toHexString(o.hashCode());
    }
}
