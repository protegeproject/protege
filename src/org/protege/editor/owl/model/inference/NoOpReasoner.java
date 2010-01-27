package org.protege.editor.owl.model.inference;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.reasoner.AxiomNotInProfileException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNodeSet;
import org.semanticweb.owlapi.util.Version;


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
    
    public OWLOntology getRootOntology() {
        return rootOntology;
    }
    
    public Set<OWLAxiom> getPendingAxiomAdditions() {
        return Collections.emptySet();
    }

    public Set<OWLAxiom> getPendingAxiomRemovals() {
        return Collections.emptySet();
    }

    public List<OWLOntologyChange> getPendingChanges() {
        return Collections.emptyList();
    }

    public BufferingMode getBufferingMode() {
        return BufferingMode.NON_BUFFERING;
    }

    public long getTimeOut() {
        return 0;
    }

    public void prepareReasoner() throws ReasonerInterruptedException, TimeOutException {
        
    }

    public void interrupt() {
        
    }

    public void dispose() {
    }

    public void flush() {
    }

    public boolean isConsistent() throws ReasonerInterruptedException, TimeOutException {
        return true;
    }

    public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty pe, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLClassNodeSet(OWLClassNode.getTopNode());
    }

    public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual ind, OWLDataProperty pe) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return Collections.emptySet();
    }

    public Node<OWLClass> getEquivalentClasses(OWLClassExpression ce) throws InconsistentOntologyException, ClassExpressionNotInProfileException, ReasonerInterruptedException, TimeOutException {
        if (ce.isAnonymous()) {
            return new OWLClassNode();
        }
        else {
            return new OWLClassNode(ce.asOWLClass());
        }
    }

    public Node<OWLDataProperty> getEquivalentDataProperties(OWLDataProperty pe) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        if (pe.isAnonymous()) {
            return new OWLDataPropertyNode();
        }
        else {
            return new OWLDataPropertyNode(pe.asOWLDataProperty());
        }
    }

    public Node<OWLObjectProperty> getEquivalentObjectProperties(OWLObjectPropertyExpression pe) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        if (pe.isAnonymous()) {
            return new OWLObjectPropertyNode();
        }
        else {
            return new OWLObjectPropertyNode(pe.asOWLObjectProperty());
        }
    }

    public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression ce, boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, ReasonerInterruptedException, TimeOutException {
        return new OWLNamedIndividualNodeSet();
    }

    public Node<OWLObjectProperty> getInverseObjectProperties(OWLObjectPropertyExpression pe) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        if (pe instanceof OWLObjectInverseOf && !((OWLObjectInverseOf) pe).getInverse().isAnonymous()) {
            return new OWLObjectPropertyNode(((OWLObjectInverseOf) pe).getInverse().asOWLObjectProperty());
        }
        else {
            return new OWLObjectPropertyNode();
        }
    }

    public NodeSet<OWLClass> getObjectPropertyDomains(OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLClassNodeSet(OWLClassNode.getTopNode());
    }

    public NodeSet<OWLClass> getObjectPropertyRanges(OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLClassNodeSet(OWLClassNode.getTopNode());
    }

    public NodeSet<OWLNamedIndividual> getObjectPropertyValues(OWLNamedIndividual ind, OWLObjectPropertyExpression pe) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLNamedIndividualNodeSet();
    }



    public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual ind) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLNamedIndividualNode(ind);
    }

    public NodeSet<OWLClass> getSubClasses(OWLClassExpression ce, boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, ReasonerInterruptedException, TimeOutException {
        return new OWLClassNodeSet();
    }

    public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty pe, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLDataPropertyNodeSet();
    }

    public NodeSet<OWLObjectProperty> getSubObjectProperties(OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLObjectPropertyNodeSet();
    }

    public NodeSet<OWLClass> getSuperClasses(OWLClassExpression ce, boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, ReasonerInterruptedException, TimeOutException {
        return new OWLClassNodeSet();
    }

    public NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty pe, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLDataPropertyNodeSet();
    }

    public NodeSet<OWLObjectProperty> getSuperObjectProperties(OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLObjectPropertyNodeSet();
    }

    public NodeSet<OWLClass> getTypes(OWLNamedIndividual ind, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLClassNodeSet();
    }

    public Node<OWLClass> getUnsatisfiableClasses() throws ReasonerInterruptedException, TimeOutException {
        return new OWLClassNode();
    }

    public boolean isEntailed(OWLAxiom axiom) throws ReasonerInterruptedException, UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException, InconsistentOntologyException {
        return false;
    }

    public boolean isEntailed(Set<? extends OWLAxiom> axioms) throws ReasonerInterruptedException, UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException, InconsistentOntologyException {
        return false;
    }

    public boolean isEntailmentCheckingSupported(AxiomType<?> axiomType) {
        return false;
    }

    public boolean isSatisfiable(OWLClassExpression classExpression) throws ReasonerInterruptedException, TimeOutException, ClassExpressionNotInProfileException, InconsistentOntologyException {
        return true;
    }

    public Node<OWLClass> getBottomClassNode() {
        OWLDataFactory factory = rootOntology.getOWLOntologyManager().getOWLDataFactory();
        return new OWLClassNode(factory.getOWLNothing());
    }

    public Node<OWLDataProperty> getBottomDataPropertyNode() {
        OWLDataFactory factory = rootOntology.getOWLOntologyManager().getOWLDataFactory();
        return new OWLDataPropertyNode(factory.getOWLBottomDataProperty());
    }

    public Node<OWLObjectProperty> getBottomObjectPropertyNode() {
        OWLDataFactory factory = rootOntology.getOWLOntologyManager().getOWLDataFactory();
        return new OWLObjectPropertyNode(factory.getOWLBottomObjectProperty());
    }

    public NodeSet<OWLNamedIndividual> getDifferentIndividuals(OWLNamedIndividual ind) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLNamedIndividualNodeSet();
    }

    public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression ce, boolean direct) {
        return new OWLClassNodeSet();
    }

    public NodeSet<OWLDataProperty> getDisjointDataProperties(OWLDataPropertyExpression pe, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLDataPropertyNodeSet();
    }

    public NodeSet<OWLObjectProperty> getDisjointObjectProperties(OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLObjectPropertyNodeSet();
    }

    public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
        return IndividualNodeSetPolicy.BY_SAME_AS;
    }

    public String getReasonerName() {
        return "Protégé Null Reasoner";
    }

    public Version getReasonerVersion() {
        return new Version(1, 0, 0, 0);
    }

    public Node<OWLClass> getTopClassNode() {
        OWLDataFactory factory = rootOntology.getOWLOntologyManager().getOWLDataFactory();
        return new OWLClassNode(factory.getOWLThing());
    }

    public Node<OWLDataProperty> getTopDataPropertyNode() {
        OWLDataFactory factory = rootOntology.getOWLOntologyManager().getOWLDataFactory();
        return new OWLDataPropertyNode(factory.getOWLTopDataProperty());
    }

    public Node<OWLObjectProperty> getTopObjectPropertyNode() {
        OWLDataFactory factory = rootOntology.getOWLOntologyManager().getOWLDataFactory();
        return new OWLObjectPropertyNode(factory.getOWLTopObjectProperty());
    }


    public FreshEntityPolicy getFreshEntityPolicy() {
        return FreshEntityPolicy.ALLOW;
    }
}
