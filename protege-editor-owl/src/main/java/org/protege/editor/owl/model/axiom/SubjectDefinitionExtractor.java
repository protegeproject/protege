package org.protege.editor.owl.model.axiom;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public interface SubjectDefinitionExtractor {

    /**
     * Gets the defining axioms in the specified ontology
     * @param subject The subject whose defining axioms are to be retrieved.  Not {@code null}.
     * @param ontology The ontology from which to retrive the axioms.  Not {@code null}.
     * @return The (possibly empty) set of defining axioms.
     */
    Set<OWLAxiom> getDefiningAxioms(OWLObject subject, OWLOntology ontology);
}
