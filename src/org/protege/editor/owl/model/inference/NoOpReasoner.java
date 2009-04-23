package org.protege.editor.owl.model.inference;

import org.semanticweb.owl.inference.MonitorableOWLReasonerAdapter;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Apr-2007<br><br>
 */
public class NoOpReasoner extends MonitorableOWLReasonerAdapter {

    public NoOpReasoner(OWLOntologyManager manager) {
        super(manager);
    }


    public boolean isConsistent(OWLOntology ontology) throws OWLReasonerException {
        return true;
    }


    public void loadOntologies(Set<OWLOntology> ontologies) throws OWLReasonerException {
    }


    public OWLEntity getCurrentEntity() {
        return null;
    }


    public boolean isClassified() throws OWLReasonerException {
        return false;
    }


    public void classify() throws OWLReasonerException {


    }


    public boolean isRealised() throws OWLReasonerException {
        return false;
    }


    public void realise() throws OWLReasonerException {
    }


    protected void disposeReasoner() {
    }


    protected void ontologiesCleared() throws OWLReasonerException {
    }


    protected void ontologiesChanged() throws OWLReasonerException {
    }


    protected void handleOntologyChanges(List<OWLOntologyChange> changes) throws OWLException {
    }


    public Set<OWLOntology> getLoadedOntologies() {
        return Collections.emptySet();
    }


    public void unloadOntologies(Set<OWLOntology> ontologies) throws OWLReasonerException {
    }


    public void clearOntologies() throws OWLReasonerException {
    }


    public boolean isSubClassOf(OWLClassExpression clsC, OWLClassExpression clsD) throws OWLReasonerException {
        return false;
    }


    public boolean isEquivalentClass(OWLClassExpression clsC, OWLClassExpression clsD) throws OWLReasonerException {
        return false;
    }


    public Set<Set<OWLClass>> getSuperClasses(OWLClassExpression clsC) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLClass>> getAncestorClasses(OWLClassExpression clsC) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLClass>> getSubClasses(OWLClassExpression clsC) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLClass>> getDescendantClasses(OWLClassExpression clsC) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<OWLClass> getEquivalentClasses(OWLClassExpression clsC) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<OWLClass> getInconsistentClasses() throws OWLReasonerException {
        return Collections.emptySet();
    }


    public boolean isSatisfiable(OWLClassExpression description) throws OWLReasonerException {
        return true;
    }


    public Set<Set<OWLClass>> getTypes(OWLIndividual individual, boolean direct) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<OWLIndividual> getIndividuals(OWLClassExpression clsC, boolean direct) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Map<OWLObjectProperty, Set<OWLIndividual>> getObjectPropertyRelationships(OWLIndividual individual) throws
                                                                                                               OWLReasonerException {
        return Collections.emptyMap();
    }


    public Map<OWLDataProperty, Set<OWLLiteral>> getDataPropertyRelationships(OWLIndividual individual) throws
                                                                                                              OWLReasonerException {
        return Collections.emptyMap();
    }


    /*
    * I have modified the signature of this method.  The OWLClassExpression parameter was
    * changed to an OWLClass parameter.
    *
    */
    public boolean hasType(OWLIndividual individual, OWLClassExpression type, boolean direct) throws OWLReasonerException {
        return false;
    }


    public boolean hasObjectPropertyRelationship(OWLIndividual subject, OWLObjectPropertyExpression property,
                                                 OWLIndividual object) throws OWLReasonerException {
        return false;
    }


    public boolean hasDataPropertyRelationship(OWLIndividual subject, OWLDataPropertyExpression property,
                                               OWLLiteral object) throws OWLReasonerException {
        return false;
    }


    public Set<OWLIndividual> getRelatedIndividuals(OWLIndividual subject, OWLObjectPropertyExpression property) throws
                                                                                                                 OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<OWLLiteral> getRelatedValues(OWLIndividual subject, OWLDataPropertyExpression property) throws
                                                                                                        OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLObjectProperty>> getSuperProperties(OWLObjectProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLObjectProperty>> getSubProperties(OWLObjectProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLObjectProperty>> getAncestorProperties(OWLObjectProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLObjectProperty>> getDescendantProperties(OWLObjectProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLObjectProperty>> getInverseProperties(OWLObjectProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<OWLObjectProperty> getEquivalentProperties(OWLObjectProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLClassExpression>> getDomains(OWLObjectProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<OWLClassExpression> getRanges(OWLObjectProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public boolean isFunctional(OWLObjectProperty property) throws OWLReasonerException {
        return false;
    }


    public boolean isInverseFunctional(OWLObjectProperty property) throws OWLReasonerException {
        return false;
    }


    public boolean isSymmetric(OWLObjectProperty property) throws OWLReasonerException {
        return false;
    }


    public boolean isTransitive(OWLObjectProperty property) throws OWLReasonerException {
        return false;
    }


    public boolean isReflexive(OWLObjectProperty property) throws OWLReasonerException {
        return false;
    }


    public boolean isIrreflexive(OWLObjectProperty property) throws OWLReasonerException {
        return false;
    }


    public boolean isAntiSymmetric(OWLObjectProperty property) throws OWLReasonerException {
        return false;
    }


    public Set<Set<OWLDataProperty>> getSuperProperties(OWLDataProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLDataProperty>> getSubProperties(OWLDataProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLDataProperty>> getAncestorProperties(OWLDataProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLDataProperty>> getDescendantProperties(OWLDataProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<OWLDataProperty> getEquivalentProperties(OWLDataProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLClassExpression>> getDomains(OWLDataProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<OWLDataRange> getRanges(OWLDataProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public boolean isFunctional(OWLDataProperty property) throws OWLReasonerException {
        return false;
    }


    public boolean isDefined(OWLClass cls) throws OWLReasonerException {
        return true;
    }


    public boolean isDefined(OWLObjectProperty prop) throws OWLReasonerException {
        return true;
    }


    public boolean isDefined(OWLDataProperty prop) throws OWLReasonerException {
        return true;
    }


    public boolean isDefined(OWLIndividual ind) throws OWLReasonerException {
        return true;
    }
}
