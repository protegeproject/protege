package org.protege.editor.owl.model.inference;

import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Apr-2007<br><br>
 */
public interface ProtegeOWLReasonerFactory extends ProtegePluginInstance {

    /**
     * This is called by Protege to configure the factory during the initialization of the 
     * reasoner plugins.
     * 
     * @param manager
     * @param id
     * @param name
     */
    void setup(OWLOntologyManager manager, String id, String name);


    /**
     * Gets the id of the reasoner that this factory creates
     */
    String getReasonerId();


    /**
     * Gets the name of the reasoner that this factory creates.
     */
    String getReasonerName();
   
    /**
     * Creates an instance of an <code>OWLReasoner</code>.  This method will
     * create a <i>new</i> instance.
     * 
     * @param ontology
     * @param monitor  A progress monitor to be used by the reasoner if possible.
     *
     */
    OWLReasoner createReasoner(OWLOntology ontology, 
                               ReasonerProgressMonitor monitor);
}
