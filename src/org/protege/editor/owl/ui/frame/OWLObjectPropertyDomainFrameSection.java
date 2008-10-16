package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.property.AbstractPropertyDomainFrameSection;
import org.protege.editor.owl.ui.frame.property.AbstractPropertyDomainFrameSectionRow;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLObjectPropertyDomainFrameSection extends AbstractPropertyDomainFrameSection<OWLObjectProperty, OWLObjectPropertyDomainAxiom> {

    public OWLObjectPropertyDomainFrameSection(OWLEditorKit editorKit, OWLFrame<OWLObjectProperty> owlObjectPropertyOWLFrame) {
        super(editorKit, owlObjectPropertyOWLFrame);
    }


    protected OWLObjectPropertyDomainAxiom createAxiom(OWLDescription object) {
        return getOWLDataFactory().getOWLObjectPropertyDomainAxiom(getRootObject(), object);
    }


    protected AbstractPropertyDomainFrameSectionRow<OWLObjectProperty, OWLObjectPropertyDomainAxiom> createFrameSectionRow(OWLObjectPropertyDomainAxiom domainAxiom, OWLOntology ontology) {
        return new OWLObjectPropertyDomainFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), domainAxiom);
    }


    protected Set<OWLObjectPropertyDomainAxiom> getAxioms(OWLOntology ontology) {
        return ontology.getObjectPropertyDomainAxioms(getRootObject());
    }


    protected Set<Set<OWLDescription>> getInferredDomains() throws OWLReasonerException {
        return getOWLModelManager().getReasoner().getDomains(getRootObject());
    }


    public void visit(OWLObjectPropertyDomainAxiom axiom) {
        if (axiom.getProperty().equals(getRootObject())) {
            reset();
        }
    }
}
