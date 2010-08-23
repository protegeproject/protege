package org.protege.editor.owl.model.inference;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

public abstract class AbstractProtegeOWLReasonerInfo implements ProtegeOWLReasonerInfo {

    private String id;

    private String name;

    private OWLOntologyManager owlOntologyManager;

    private OWLModelManager owlModelManager;

    public void setup(OWLOntologyManager manager, String id, String name) {
        this.id = id;
        this.name = name;
        this.owlOntologyManager = manager;
    }
    
    public OWLModelManager getOWLModelManager() {
        return owlModelManager;
    }
    
    public void setOWLModelManager(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }
    
    public OWLOntologyManager getOWLOntologyManager() {
        return owlOntologyManager;
    }


    public String getReasonerId() {
        return id;
    }


    public String getReasonerName() {
        return name;
    }
    
    public OWLReasonerConfiguration getConfiguration(ReasonerProgressMonitor monitor) {
    	return new SimpleConfiguration(monitor);
    }
}
