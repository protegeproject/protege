package org.protege.editor.owl.model;

import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Feb 2017
 */
public class DocumentFormatUpdater {

    private static final Logger logger = LoggerFactory.getLogger(DocumentFormatUpdater.class);

    @Nonnull
    private final DocumentFormatMapper formatMapper;

    public DocumentFormatUpdater(@Nonnull DocumentFormatMapper formatMapper) {
        this.formatMapper = checkNotNull(formatMapper);
    }

    public void updateFormat(@Nonnull OWLOntology ontology) {
        checkNotNull(ontology);
        OWLOntologyManager man = ontology.getOWLOntologyManager();
        OWLDocumentFormat format = man.getOntologyFormat(ontology);
        if(format == null) {
            return;
        }
        OWLDocumentFormat mappedFormat = formatMapper.mapFormat(format);
        man.setOntologyFormat(ontology, mappedFormat);
        if(mappedFormat != format) {
            logger.info("Updated document format class from: {} to: {}",
                        format.getClass().getName(),
                        mappedFormat.getClass().getName());
        }
    }
}
