package org.protege.editor.owl.model.find;

import org.semanticweb.owl.model.*;

import java.util.Set;


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


    Set<OWLClass> getMatchingOWLClasses(String match);


    Set<OWLObjectProperty> getMatchingOWLObjectProperties(String match);


    Set<OWLDataProperty> getMatchingOWLDataProperties(String match);


    Set<OWLIndividual> getMatchingOWLIndividuals(String match);
}
