package org.protege.editor.owl.model.axiom;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.util.AxiomSubjectProviderEx;

import java.util.Optional;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public class DefaultAxiomSubjectProvider implements AxiomSubjectProvider {
    @Override
    public Optional<OWLObject> getAxiomSubject(OWLAxiom axiom) {
        OWLObject subject = AxiomSubjectProviderEx.getSubject(axiom);
        return Optional.ofNullable(subject);
    }
}
