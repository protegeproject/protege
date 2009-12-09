package org.protege.editor.owl.ui.frame.dataproperty;

import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.property.AbstractPropertyDomainFrameSection;
import org.protege.editor.owl.ui.frame.property.AbstractPropertyDomainFrameSectionRow;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDataPropertyDomainFrameSection extends AbstractPropertyDomainFrameSection<OWLDataProperty, OWLDataPropertyDomainAxiom> {

    public OWLDataPropertyDomainFrameSection(OWLEditorKit editorKit, OWLFrame<OWLDataProperty> owlDataPropertyOWLFrame) {
        super(editorKit, owlDataPropertyOWLFrame);
    }


    protected OWLDataPropertyDomainAxiom createAxiom(OWLClassExpression object) {
        return getOWLDataFactory().getOWLDataPropertyDomainAxiom(getRootObject(), object);
    }


    protected AbstractPropertyDomainFrameSectionRow<OWLDataProperty, OWLDataPropertyDomainAxiom> createFrameSectionRow(OWLDataPropertyDomainAxiom domainAxiom, OWLOntology ontology) {
        return new OWLDataPropertyDomainFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), domainAxiom);
    }


    protected Set<OWLDataPropertyDomainAxiom> getAxioms(OWLOntology ontology) {
        return ontology.getDataPropertyDomainAxioms(getRootObject());
    }


    protected NodeSet<OWLClass> getInferredDomains() {
        return getOWLModelManager().getReasoner().getDataPropertyDomains(getRootObject(), true);
    }


    public void visit(OWLDataPropertyDomainAxiom axiom) {
        if (axiom.getProperty().equals(getRootObject())) {
            reset();
        }
    }
}
