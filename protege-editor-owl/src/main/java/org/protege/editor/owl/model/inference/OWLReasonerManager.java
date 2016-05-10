package org.protege.editor.owl.model.inference;

import org.protege.editor.core.Disposable;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>

 * The reasoner manager manages a "global" reaoner instance, but
 * also provides the ability to create new instances of the currently
 * selected reasoner.
 */
public interface OWLReasonerManager extends Disposable {


    void setReasonerProgressMonitor(ReasonerProgressMonitor progressMonitor);


    /**
     * Gets the ID of the current reasoner.
     * @return A <code>String</code> representation of the current
     *         reasoner id.
     */
    String getCurrentReasonerFactoryId();

    ProtegeOWLReasonerInfo getCurrentReasonerFactory();

    String getCurrentReasonerName();


    /**
     * Sets the current reasoner to be the reasoner specified by the
     * id.
     * @param id The reasoner id that specified the reasoner that should
     *           be set as the current reasoner.
     */
    void setCurrentReasonerFactoryId(String id);


    /**
     * Gets the installed reasoner plugins.
     */
    Set<ProtegeOWLReasonerInfo> getInstalledReasonerFactories();


    
    /**
     * Gets the current reasoner.
     */
    OWLReasoner getCurrentReasoner();
    
    void killCurrentReasoner();

    /**
     * @deprecated use getReasonerStatus() instead
     */
    @Deprecated
    boolean isClassificationInProgress();
    
    /**
     * @deprecated use getReasonerStatus() instead.
     */
    @Deprecated
    boolean isClassified();
    
    /**
     * Returns the current status of the reasoner.
     * 
     * @throws ReasonerDiedException if the reasoner went into a bad state and needed to be trashed
     *                                then this exception is thrown.
     */
    ReasonerStatus getReasonerStatus();

    boolean classifyAsynchronously(Set<InferenceType> precompute);

    void setReasonerExceptionHandler(OWLReasonerExceptionHandler handler);


    void killCurrentClassification();
    
    ReasonerPreferences getReasonerPreferences();
    
    /**
     * Add the filters to be applied when the ABOUT_TO_CLASSIFY event starts.
     * The list of filters will be cleared when the classification completes regardless
     * of whether a new reasoner was created in the process.
     * 
     * @param filter
     */
    void addReasonerFilter(ReasonerFilter filter);
    
}
