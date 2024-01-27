package org.protege.editor.owl.ui.action.export.inferred;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredClassAxiomGenerator;

public class InferredDisjointClassesAxiomGenerator extends InferredClassAxiomGenerator<OWLDisjointClassesAxiom> {

	@Override
	protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLDisjointClassesAxiom> result) {
		for (OWLClass disjoint : reasoner.getDisjointClasses(entity).getFlattened()) {
			result.add(dataFactory.getOWLDisjointClassesAxiom(entity, disjoint));
		}
	}

    public String getLabel() {
        return "Disjoint classes";
    }

}
