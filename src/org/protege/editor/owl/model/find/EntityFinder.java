package org.protege.editor.owl.model.find;

import java.util.Set;

import org.semanticweb.owl.model.OWLEntity;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 16-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface EntityFinder {

    /**
     * Gets the entities that match the specified string.
     */
    Set<OWLEntity> getEntities(String match);


    Set<OWLEntity> getMatchingClasses(String match);
}
