package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.semanticweb.owlapi.model.OWLObject;

import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: May 4, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface AutoCompleterMatcher {

    Set<OWLObject> getMatches(String fragment, boolean classes, boolean objectProperties, boolean dataProperties,
    		                  boolean individuals, boolean datatypes, boolean annotationProperties);
}
