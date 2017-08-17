package org.protege.editor.owl.model.axiom;

import com.google.common.base.Optional;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public class DefaultAxiomSubjectProvider implements AxiomSubjectProvider {
    @Override
    public Optional<OWLObject> getAxiomSubject(OWLAxiom axiom) {
        org.semanticweb.owlapi.util.AxiomSubjectProviderEx provider = new org.semanticweb.owlapi.util.AxiomSubjectProviderEx();
        OWLObject subject = provider.getSubject(axiom);
        return Optional.fromNullable(subject);
    }
}
