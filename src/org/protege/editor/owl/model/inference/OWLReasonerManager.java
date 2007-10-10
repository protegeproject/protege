package org.protege.editor.owl.model.inference;

import java.util.Set;

import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.util.ProgressMonitor;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * The reasoner manager manages a "global" reaoner instance, but
 * also provides the ability to create new instances of the currently
 * selected reasoner.
 */
public interface OWLReasonerManager {

    public void dispose();


    public void setReasonerProgressMonitor(ProgressMonitor progressMonitor);


    /**
     * Gets the ID of the current reasoner.
     * @return A <code>String</code> representation of the current
     *         reasoner id.
     */
    public String getCurrentReasonerFactoryId();


    /**
     * Sets the current reasoner to be the reasoner specified by the
     * id.
     * @param id The reasoner id that specified the reasoner that should
     *           be set as the current reasoner.
     */
    public void setCurrentReasonerFactoryId(String id);


    /**
     * Gets the installed reasoner plugins.
     */
    public Set<ProtegeOWLReasonerFactory> getInstalledReasonerFactories();


    /**
     * Gets the current reasoner.
     */
    public OWLReasoner getCurrentReasoner();


    public OWLReasoner createReasoner(OWLOntologyManager owlOntologyManager);


    public void classifyAsynchronously();
}
