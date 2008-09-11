package org.protege.editor.owl.model.inference;

import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerFactory;
import org.semanticweb.owl.model.OWLOntologyManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Apr-2007<br><br>
 */
public interface ProtegeOWLReasonerFactory extends ProtegePluginInstance, OWLReasonerFactory {

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
     * @param owlOntologyManager The manager to be used by the reaoner.
     */
    OWLReasoner createReasoner(OWLOntologyManager owlOntologyManager);

    
}
