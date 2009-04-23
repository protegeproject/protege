package org.protege.editor.owl.ui.frame.dataproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.property.AbstractPropertyDomainFrameSection;
import org.protege.editor.owl.ui.frame.property.AbstractPropertyDomainFrameSectionRow;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLClassExpression;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.util.Set;


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


    protected Set<Set<OWLClassExpression>> getInferredDomains() throws OWLReasonerException {
        return getOWLModelManager().getReasoner().getDomains(getRootObject());
    }


    public void visit(OWLDataPropertyDomainAxiom axiom) {
        if (axiom.getProperty().equals(getRootObject())) {
            reset();
        }
    }
}
