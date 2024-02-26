package org.protege.editor.owl.ui.frame.dataproperty;

import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.property.AbstractPropertyDomainFrameSection;
import org.protege.editor.owl.ui.frame.property.AbstractPropertyDomainFrameSectionRow;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;


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
        OWLReasoner reasoner = getOWLModelManager().getReasoner();
        OWLDataProperty p = getRootObject();
        OWLDataFactory factory = getOWLModelManager().getOWLOntologyManager().getOWLDataFactory();
        if (p.equals(factory.getOWLTopDataProperty())) {
            return new OWLClassNodeSet(reasoner.getTopClassNode());
        }
        OWLClassExpression domain = factory.getOWLDataSomeValuesFrom(p, factory.getTopDatatype());
        Node<OWLClass> domainNode = reasoner.getEquivalentClasses(domain);
        if (domainNode != null && !domainNode.getEntities().isEmpty()) {
            return new OWLClassNodeSet(domainNode);
        }
        else {
            return reasoner.getDataPropertyDomains(getRootObject(), true);
        }
    }
    
    @Override
    protected void refillInferred() {
        getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERRED_DATATYPE_PROPERTY_DOMAINS,
                () -> {
                    if (!getOWLModelManager().getReasoner().isConsistent()) {
                        return;
                    }
                    OWLDataPropertyDomainFrameSection.super.refillInferred();
                });
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	return change.isAxiomChange() &&
    			change.getAxiom() instanceof OWLDataPropertyDomainAxiom &&
    			((OWLDataPropertyDomainAxiom) change.getAxiom()).getProperty().equals(getRootObject());
    }
    
}
