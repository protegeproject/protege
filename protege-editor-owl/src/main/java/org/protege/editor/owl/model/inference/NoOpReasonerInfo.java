package org.protege.editor.owl.model.inference;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.*;


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

    public void setup(OWLOntologyManager manager, String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getReasonerId() {
        return id;
    }

    public String getReasonerName() {
        return name;
    }


    
    public OWLReasoner createReasoner(OWLOntology ontology, ReasonerProgressMonitor monitor) {
        return new NoOpReasoner(ontology);
    }

    public void initialise() throws Exception {

    }

    public void dispose() throws Exception {

    }

	public void setOWLModelManager(OWLModelManager owlModelManager) {
		manager = owlModelManager;
	}
	
	public OWLModelManager getOWLModelManager() {
		return manager;
	}
	
	public BufferingMode getRecommendedBuffering() {
		return BufferingMode.NON_BUFFERING;
	}

	public OWLReasonerConfiguration getConfiguration(ReasonerProgressMonitor monitor) {
		return new SimpleConfiguration(monitor);
	}

	public OWLReasonerFactory getReasonerFactory() {
		return new NoOpReasonerFactory();
	}

}
