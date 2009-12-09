package org.protege.editor.owl.model.inference;

import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.reasoner.AxiomNotInProfileException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UndeclaredEntitiesException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Apr-2007<br><br>
 */
public class NoOpReasoner implements OWLReasoner {
    private OWLOntology rootOntology;

    public NoOpReasoner(OWLOntology rootOntology) {
        this.rootOntology = rootOntology;
    }

    public void dispose() {
    }

    public void flush() {
    }

    public BufferingMode getBufferingMode() {
        return BufferingMode.NON_BUFFERING;
    }

    public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty pe, boolean direct) throws InconsistentOntologyException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual ind, OWLDataProperty pe) throws InconsistentOntologyException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public Node<OWLClass> getEquivalentClasses(OWLClassExpression ce) throws InconsistentOntologyException, ClassExpressionNotInProfileException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public Node<OWLDataProperty> getEquivalentDataProperties(OWLDataProperty pe) throws InconsistentOntologyException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public Node<OWLObjectProperty> getEquivalentObjectProperties(OWLObjectPropertyExpression pe) throws InconsistentOntologyException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression ce, boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public Node<OWLObjectProperty> getInverseObjectProperties(OWLObjectPropertyExpression pe) throws InconsistentOntologyException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public NodeSet<OWLClass> getObjectPropertyDomains(OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public NodeSet<OWLClass> getObjectPropertyRanges(OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public NodeSet<OWLNamedIndividual> getObjectPropertyValues(OWLNamedIndividual ind, OWLObjectPropertyExpression pe) throws InconsistentOntologyException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public Set<OWLAxiom> getPendingAxiomAdditions() {
        return null;
    }

    public Set<OWLAxiom> getPendingAxiomRemovals() {
        return null;
    }

    public List<OWLOntologyChange> getPendingChanges() {
        return null;
    }

    public OWLOntology getRootOntology() {
        return rootOntology;
    }

    public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual ind) throws InconsistentOntologyException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public NodeSet<OWLClass> getSubClasses(OWLClassExpression ce, boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty pe, boolean direct) throws InconsistentOntologyException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public NodeSet<OWLObjectProperty> getSubObjectProperties(OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public NodeSet<OWLClass> getSuperClasses(OWLClassExpression ce, boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty pe, boolean direct) throws InconsistentOntologyException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public NodeSet<OWLObjectProperty> getSuperObjectProperties(OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public long getTimeOut() {
        return 0;
    }

    public NodeSet<OWLClass> getTypes(OWLNamedIndividual ind, boolean direct) throws InconsistentOntologyException, UndeclaredEntitiesException, ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public Node<OWLClass> getUnsatisfiableClasses() throws ReasonerInterruptedException, TimeOutException {
        return null;
    }

    public void interrupt() {
        
    }

    public boolean isConsistent() throws ReasonerInterruptedException, TimeOutException {
        return true;
    }

    public boolean isEntailed(OWLAxiom axiom) throws ReasonerInterruptedException, UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException, UndeclaredEntitiesException, InconsistentOntologyException {
        return false;
    }

    public boolean isEntailed(Set<? extends OWLAxiom> axioms) throws ReasonerInterruptedException, UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException, UndeclaredEntitiesException, InconsistentOntologyException {
        return false;
    }

    public boolean isEntailmentCheckingSupported(AxiomType<?> axiomType) {
        return false;
    }

    public boolean isSatisfiable(OWLClassExpression classExpression) throws ReasonerInterruptedException, TimeOutException, ClassExpressionNotInProfileException, UndeclaredEntitiesException, InconsistentOntologyException {
        return true;
    }

    public void prepareReasoner() throws ReasonerInterruptedException, TimeOutException {
        
    }

    
}
