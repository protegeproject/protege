package org.protege.editor.owl.ui.metrics;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Feb-2007<br><br>
 */
public abstract class Metric {

    private String name;

    private OWLModelManager owlModelManager;


    protected Metric(String name) {
        this.name = name;
    }


    protected void setOWLModelManager(OWLModelManager modelManager) {
        this.owlModelManager = modelManager;
    }


    public String getName() {
        return name;
    }


    protected OWLModelManager getOWLModelManager() {
        return owlModelManager;
    }


    public String toString() {
        return getName() + ": " + getMetric();
    }


    public abstract String getMetric();


    public abstract void updateMetric();

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
    }


    public void visit(OWLAnnotationAxiom axiom) {
    }


    public void visit(OWLClassAssertionAxiom axiom) {
    }


    public void visit(OWLDataPropertyAssertionAxiom axiom) {
    }


    public void visit(OWLDataPropertyDomainAxiom axiom) {
    }


    public void visit(OWLDataPropertyRangeAxiom axiom) {
    }


    public void visit(OWLSubDataPropertyOfAxiom axiom) {
    }


    public void visit(OWLDeclarationAxiom axiom) {
    }


    public void visit(OWLDifferentIndividualsAxiom axiom) {
    }


    public void visit(OWLDisjointClassesAxiom axiom) {
    }


    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
    }


    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
    }


    public void visit(OWLDisjointUnionAxiom axiom) {
    }


    public void visit(OWLEquivalentClassesAxiom axiom) {
    }


    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
    }


    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
    }


    public void visit(OWLFunctionalDataPropertyAxiom axiom) {
    }


    public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
    }


    public void visit(OWLImportsDeclaration axiom) {
    }


    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
    }


    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
    }


    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
    }


    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
    }


    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
    }


    public void visit(OWLObjectPropertyAssertionAxiom axiom) {
    }


    public void visit(OWLSubPropertyChainOfAxiom axiom) {
    }


    public void visit(OWLObjectPropertyDomainAxiom axiom) {
    }


    public void visit(OWLObjectPropertyRangeAxiom axiom) {
    }


    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
    }


    public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
    }


    public void visit(OWLSameIndividualAxiom axiom) {
    }


    public void visit(OWLSubClassOfAxiom axiom) {
    }


    public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
    }


    public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
    }


    public void visit(SWRLRule rule) {
    }
}
