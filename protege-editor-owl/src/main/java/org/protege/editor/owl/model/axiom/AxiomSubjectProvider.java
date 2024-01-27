package org.protege.editor.owl.model.axiom;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;

import com.google.common.base.Optional;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public interface AxiomSubjectProvider {

    Optional<OWLObject> getAxiomSubject(OWLAxiom axiom);
}
