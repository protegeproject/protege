package org.protege.editor.owl.model.inference;

import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Apr-2007<br><br>
 */
public interface ProtegeOWLReasonerInfo extends ProtegePluginInstance {

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
    
    void setOWLModelManager(OWLModelManager owlModelManager);
    
    OWLModelManager getOWLModelManager();
   
    BufferingMode getRecommendedBuffering();
    
    OWLReasonerConfiguration getConfiguration(ReasonerProgressMonitor monitor);
    
    OWLReasonerFactory getReasonerFactory();
}
