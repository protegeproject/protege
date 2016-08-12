package org.protege.editor.owl.model.inference;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.impl.*;
import org.semanticweb.owlapi.util.Version;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Apr-2007<br><br>
 */
public class NoOpReasoner implements OWLReasoner {

    private final OWLOntology rootOntology;

    private final OWLClass OWL_THING;

    private final OWLClass OWL_NOTHING;

    private final OWLObjectProperty OWL_TOP_OBJECT_PROPERTY;

    private final OWLObjectProperty OWL_BOTTOM_OBJECT_PROPERTY;

    private final OWLDataProperty OWL_TOP_DATA_PROPERTY;

    private final OWLDataProperty OWL_BOTTOM_DATA_PROPERTY;


    public NoOpReasoner(OWLOntology rootOntology) {
        this.rootOntology = rootOntology;
        OWLDataFactory df = rootOntology.getOWLOntologyManager().getOWLDataFactory();
        OWL_THING = df.getOWLThing();
        OWL_NOTHING = df.getOWLNothing();
        OWL_TOP_OBJECT_PROPERTY = df.getOWLTopObjectProperty();
        OWL_BOTTOM_OBJECT_PROPERTY = df.getOWLBottomObjectProperty();
        OWL_TOP_DATA_PROPERTY = df.getOWLTopDataProperty();
        OWL_BOTTOM_DATA_PROPERTY = df.getOWLBottomDataProperty();
    }

    @Nonnull
    public OWLOntology getRootOntology() {
        return rootOntology;
    }

    @Nonnull
    public Set<OWLAxiom> getPendingAxiomAdditions() {
        return Collections.emptySet();
    }

    @Nonnull
    public Set<OWLAxiom> getPendingAxiomRemovals() {
        return Collections.emptySet();
    }

    @Nonnull
    public List<OWLOntologyChange> getPendingChanges() {
        return Collections.emptyList();
    }

    @Nonnull
    public BufferingMode getBufferingMode() {
        return BufferingMode.NON_BUFFERING;
    }

    public long getTimeOut() {
        return 0;
    }

    @Nonnull
    public Set<InferenceType> getPrecomputableInferenceTypes() {
        return Collections.emptySet();
    }

    public boolean isPrecomputed(@Nonnull InferenceType inferenceType) {
        return true;
    }
    
    public void precomputeInferences(@Nonnull InferenceType... inferenceTypes) throws ReasonerInterruptedException, TimeOutException, InconsistentOntologyException {
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

    @Nonnull
    public NodeSet<OWLClass> getDataPropertyDomains(@Nonnull OWLDataProperty pe, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLClassNodeSet();
    }

    @Nonnull
    public Set<OWLLiteral> getDataPropertyValues(@Nonnull OWLNamedIndividual ind, @Nonnull OWLDataProperty pe) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return Collections.emptySet();
    }

    @Nonnull
    public Node<OWLClass> getEquivalentClasses(@Nonnull OWLClassExpression ce) throws InconsistentOntologyException, ClassExpressionNotInProfileException, ReasonerInterruptedException, TimeOutException {
        if (ce.isAnonymous()) {
            return new OWLClassNode();
        }
        else {
            return new OWLClassNode(ce.asOWLClass());
        }
    }

    @Nonnull
    public Node<OWLDataProperty> getEquivalentDataProperties(@Nonnull OWLDataProperty pe) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        if (pe.isAnonymous()) {
            return new OWLDataPropertyNode();
        }
        else {
            return new OWLDataPropertyNode(pe.asOWLDataProperty());
        }
    }

    @Nonnull
    public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(@Nonnull OWLObjectPropertyExpression pe) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        if (pe.isAnonymous()) {
            return new OWLObjectPropertyNode();
        }
        else {
            return new OWLObjectPropertyNode(pe.asOWLObjectProperty());
        }
    }

    @Nonnull
    public NodeSet<OWLNamedIndividual> getInstances(@Nonnull OWLClassExpression ce, boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, ReasonerInterruptedException, TimeOutException {
        return new OWLNamedIndividualNodeSet();
    }

    @Nonnull
    public Node<OWLObjectPropertyExpression> getInverseObjectProperties(@Nonnull OWLObjectPropertyExpression pe) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLObjectPropertyNode();
    }

    @Nonnull
    public NodeSet<OWLClass> getObjectPropertyDomains(@Nonnull OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLClassNodeSet();
    }

    @Nonnull
    public NodeSet<OWLClass> getObjectPropertyRanges(@Nonnull OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLClassNodeSet();
    }

    @Nonnull
    public NodeSet<OWLNamedIndividual> getObjectPropertyValues(@Nonnull OWLNamedIndividual ind, @Nonnull OWLObjectPropertyExpression pe) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLNamedIndividualNodeSet();
    }


    @Nonnull
    public Node<OWLNamedIndividual> getSameIndividuals(@Nonnull OWLNamedIndividual ind) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLNamedIndividualNode(ind);
    }

    @Nonnull
    public NodeSet<OWLClass> getSubClasses(@Nonnull OWLClassExpression ce, boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, ReasonerInterruptedException, TimeOutException {
        return new OWLClassNodeSet();
    }

    @Nonnull
    public NodeSet<OWLDataProperty> getSubDataProperties(@Nonnull OWLDataProperty pe, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLDataPropertyNodeSet();
    }

    @Nonnull
    public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(@Nonnull OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLObjectPropertyNodeSet();
    }

    @Nonnull
    public NodeSet<OWLClass> getSuperClasses(@Nonnull OWLClassExpression ce, boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, ReasonerInterruptedException, TimeOutException {
        return new OWLClassNodeSet();
    }

    @Nonnull
    public NodeSet<OWLDataProperty> getSuperDataProperties(@Nonnull OWLDataProperty pe, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLDataPropertyNodeSet();
    }

    @Nonnull
    public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(@Nonnull OWLObjectPropertyExpression pe, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLObjectPropertyNodeSet();
    }

    @Nonnull
    public NodeSet<OWLClass> getTypes(@Nonnull OWLNamedIndividual ind, boolean direct) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLClassNodeSet();
    }

    @Nonnull
    public Node<OWLClass> getUnsatisfiableClasses() throws ReasonerInterruptedException, TimeOutException {
        return new OWLClassNode();
    }

    public boolean isEntailed(@Nonnull OWLAxiom axiom) throws ReasonerInterruptedException, UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException, InconsistentOntologyException {
        return false;
    }

    public boolean isEntailed(@Nonnull Set<? extends OWLAxiom> axioms) throws ReasonerInterruptedException, UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException, InconsistentOntologyException {
        return false;
    }

    public boolean isEntailmentCheckingSupported(@Nonnull AxiomType<?> axiomType) {
        return false;
    }

    public boolean isSatisfiable(@Nonnull OWLClassExpression classExpression) throws ReasonerInterruptedException, TimeOutException, ClassExpressionNotInProfileException, InconsistentOntologyException {
        return true;
    }

    @Nonnull
    public Node<OWLClass> getBottomClassNode() {
        return new OWLClassNode(OWL_NOTHING);
    }

    @Nonnull
    public Node<OWLDataProperty> getBottomDataPropertyNode() {
        return new OWLDataPropertyNode(OWL_BOTTOM_DATA_PROPERTY);
    }

    @Nonnull
    public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
        return new OWLObjectPropertyNode(OWL_BOTTOM_OBJECT_PROPERTY);
    }

    @Nonnull
    public NodeSet<OWLNamedIndividual> getDifferentIndividuals(@Nonnull OWLNamedIndividual ind) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLNamedIndividualNodeSet();
    }

    @Nonnull
    public NodeSet<OWLClass> getDisjointClasses(@Nonnull OWLClassExpression ce) {
        return new OWLClassNodeSet();
    }

    @Nonnull
    public NodeSet<OWLDataProperty> getDisjointDataProperties(@Nonnull OWLDataPropertyExpression pe) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLDataPropertyNodeSet();
    }

    @Nonnull
    public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(@Nonnull OWLObjectPropertyExpression pe) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException {
        return new OWLObjectPropertyNodeSet();
    }

    @Nonnull
    public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
        return IndividualNodeSetPolicy.BY_SAME_AS;
    }

    @Nonnull
    public String getReasonerName() {
        return "Prot\u00E9g\u00E9 Null Reasoner";
    }

    @Nonnull
    public Version getReasonerVersion() {
        return new Version(1, 0, 0, 0);
    }

    @Nonnull
    public Node<OWLClass> getTopClassNode() {
        return new OWLClassNode(OWL_THING);
    }

    @Nonnull
    public Node<OWLDataProperty> getTopDataPropertyNode() {
        return new OWLDataPropertyNode(OWL_TOP_DATA_PROPERTY);
    }

    @Nonnull
    public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
        return new OWLObjectPropertyNode(OWL_TOP_OBJECT_PROPERTY);
    }

    @Nonnull
    public FreshEntityPolicy getFreshEntityPolicy() {
        return FreshEntityPolicy.ALLOW;
    }
}
