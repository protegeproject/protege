package org.protege.editor.owl.model.io;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.net.URI;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.eclipse.jgit.annotations.NonNull;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 1, 2008<br><br>
 *
 * A custom URIMapper.  This is used by the various parsers to
 * convert ontology URIs into physical URIs that point to concrete
 * representations of ontologies.

 * The mapper uses the following strategy:

 * It looks in auto-mapped libraries.  These are folder libraries
 * that correspond to folders where the "root ontologies" have been
 * loaded from.  If the mapper finds an ontology that has a mapping
 * to one of these auto-mapped files, then the URI of the
 * auto-mapped file is returned.
 */
public class AutoMappedRepositoryIRIMapper implements OWLOntologyIRIMapper {

    private static final Logger logger = LoggerFactory.getLogger(AutoMappedRepositoryIRIMapper.class);

    private static final String FILE_SCHEME = "file";

    @NonNull
    private final OntologyCatalogManager ontologyCatalogManager;

    @NonNull
    private final Optional<URI> rootOntologyDocument;

    /**
     * Constructs an {@link AutoMappedRepositoryIRIMapper} using the specified catalog manager.
     * @deprecated Use the constructor that takes a root ontology document URI.
     */
    @Deprecated
    public AutoMappedRepositoryIRIMapper(@NonNull OntologyCatalogManager ontologyCatalogManager) {
        this.ontologyCatalogManager = checkNotNull(ontologyCatalogManager);
        rootOntologyDocument = Optional.empty();
    }

    /**
     * Constructs an {@link AutoMappedRepositoryIRIMapper}.  If the root ontology document is
     * a file URI (has the scheme "file") then sub-directories will be lazily searched for imports.
     * @param ontologyCatalogManager The catalog manager that will be used to index ontology documents
     *                               contained in sub-directories if the root ontology document is a file.
     * @param rootOntologyDocument The root ontology document.
     */
    public AutoMappedRepositoryIRIMapper(@NonNull OntologyCatalogManager ontologyCatalogManager,
                                         @NonNull URI rootOntologyDocument) {
        this.ontologyCatalogManager = checkNotNull(ontologyCatalogManager);
        this.rootOntologyDocument = Optional.of(rootOntologyDocument);
    }

    @NonNull
    public IRI getDocumentIRI(@Nonnull IRI importedIRI) {
        final URI uri = importedIRI.toURI();
        rootOntologyDocument.ifPresent(rootOntologyDocument -> {
            if(FILE_SCHEME.equals(rootOntologyDocument.getScheme())) {
                // We are being asked for a redirect for an import that is imported directly or indirectly
                // from a root ontology contained in the local file system.  Therefore, index local files
                // to see if there is a local import
                File rootOntologyDocumentDirectory = new File(rootOntologyDocument).getParentFile();
                ontologyCatalogManager.addFolder(rootOntologyDocumentDirectory);
            }
        });
    	Optional<URI> redirect = ontologyCatalogManager.getRedirectForUri(uri);
    	if (redirect.isPresent()) {
            logger.info("Imported ontology document {} was resolved to {} by the ontology catalog.", importedIRI, redirect);
        }
        else {
            logger.info("Imported ontology document {} was not resolved to any documents defined in the ontology catalog.", importedIRI);
        }
        return redirect.map(IRI::create).orElse(null);
    }
}
