package org.protege.editor.owl.model.inference;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Apr-2007<br><br>
 */
public class NoOpReasonerInfo implements ProtegeOWLReasonerInfo {

    private String id = NULL_REASONER_ID;

    private String name;

    public static final String NULL_REASONER_ID = "org.protege.editor.owl.NoOpReasoner";

    private OWLModelManager manager;


    public void setup(@Nonnull OWLOntologyManager manager, @Nonnull String id, @Nonnull String name) {
        this.id = checkNotNull(id);
        this.name = checkNotNull(name);
    }

    @Nonnull
    public String getReasonerId() {
        return id;
    }

    @Nonnull
    public String getReasonerName() {
        return name;
    }

    @Nonnull
    public OWLReasoner createReasoner(OWLOntology ontology, ReasonerProgressMonitor monitor) {
        return new NoOpReasoner(ontology);
    }

    public void initialise() throws Exception {

    }

    public void dispose() throws Exception {

    }

    public void setOWLModelManager(@Nonnull OWLModelManager owlModelManager) {
        manager = owlModelManager;
    }

    @Nonnull
    public OWLModelManager getOWLModelManager() {
        return manager;
    }

    @Nonnull
    public BufferingMode getRecommendedBuffering() {
        return BufferingMode.NON_BUFFERING;
    }

    @Nonnull
    public OWLReasonerConfiguration getConfiguration(ReasonerProgressMonitor monitor) {
        return new SimpleConfiguration(monitor);
    }

    @Nonnull
    public OWLReasonerFactory getReasonerFactory() {
        return new NoOpReasonerFactory();
    }

}
