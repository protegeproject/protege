package org.protege.editor.owl.model.hierarchy;

import org.semanticweb.owlapi.model.OWLObject;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLObjectHierarchyProviderListener<N extends OWLObject> {

    /**
     * Notifies the listener that the parents and or children
     * of the specified node might have changed.  This is usually
     * called in response to an add/remove axiom change.
     */
    public void nodeChanged(N node);


    /**
     * Notifies the listener that the complete hierarchy
     * has changed.
     */
    public void hierarchyChanged();
}
