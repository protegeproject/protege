package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class AssertUniqueNameAssumptionAction extends ProtegeOWLAction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2083948438884360191L;

	public void initialise() throws Exception {

	}

	public void dispose() throws Exception {

	}

	public void actionPerformed(ActionEvent arg0) {
		Set<OWLNamedIndividual> individuals = collectIndividuals();
		assertDistinct(individuals);
	}
	
	private Set<OWLNamedIndividual> collectIndividuals() {
		Set<OWLOntology> ontologies = getOWLModelManager().getActiveOntologies();
		Set<OWLNamedIndividual> individuals = new HashSet<OWLNamedIndividual>();
		for (OWLOntology ontology : ontologies) {
			individuals.addAll(ontology.getIndividualsInSignature());
		}
		return individuals;
	}

	private void assertDistinct(Set<OWLNamedIndividual> individuals) {
		OWLOntology ontology = getOWLModelManager().getActiveOntology();
		OWLOntologyManager manager = ontology.getOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLAxiom distinct = factory.getOWLDifferentIndividualsAxiom(individuals.toArray(new OWLNamedIndividual[0]));
		manager.addAxiom(ontology, distinct);
	}
}
