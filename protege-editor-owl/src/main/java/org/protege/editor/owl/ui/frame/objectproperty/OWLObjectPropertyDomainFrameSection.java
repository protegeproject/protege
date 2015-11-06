package org.protege.editor.owl.ui.frame.objectproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.property.AbstractPropertyDomainFrameSection;
import org.protege.editor.owl.ui.frame.property.AbstractPropertyDomainFrameSectionRow;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;

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


    protected OWLObjectPropertyDomainAxiom createAxiom(OWLClassExpression object) {
        return getOWLDataFactory().getOWLObjectPropertyDomainAxiom(getRootObject(), object);
    }


    protected AbstractPropertyDomainFrameSectionRow<OWLObjectProperty, OWLObjectPropertyDomainAxiom> createFrameSectionRow(OWLObjectPropertyDomainAxiom domainAxiom, OWLOntology ontology) {
        return new OWLObjectPropertyDomainFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), domainAxiom);
    }


    protected Set<OWLObjectPropertyDomainAxiom> getAxioms(OWLOntology ontology) {
        return ontology.getObjectPropertyDomainAxioms(getRootObject());
    }


    protected NodeSet<OWLClass> getInferredDomains() {
        OWLReasoner reasoner = getOWLModelManager().getReasoner();
        OWLObjectProperty p = getRootObject();
        OWLDataFactory factory = getOWLModelManager().getOWLOntologyManager().getOWLDataFactory();
        if (p.equals(factory.getOWLTopObjectProperty())) {
            return new OWLClassNodeSet(reasoner.getTopClassNode());
        }
        OWLClassExpression domain = factory.getOWLObjectSomeValuesFrom(p, factory.getOWLThing());
        Node<OWLClass> domainNode = reasoner.getEquivalentClasses(domain);
        if (domainNode != null && !domainNode.getEntities().isEmpty()) {
            return new OWLClassNodeSet(domainNode);
        }
        else {
            return reasoner.getObjectPropertyDomains(getRootObject(), true);
        }
    }
    
    @Override
    protected void refillInferred() {
        getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERRED_OBJECT_PROPERTY_DOMAINS,
                                                                  new Runnable() {
            public void run() {
            	if (!getOWLModelManager().getReasoner().isConsistent()) {
            		return;
            	}
                OWLObjectPropertyDomainFrameSection.super.refillInferred();
            }
        });
    }
    
    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	if (!change.isAxiomChange()) {
    		return false;
    	}
    	OWLAxiom axiom = change.getAxiom();
    	if (axiom instanceof OWLObjectPropertyDomainAxiom) {
    		return ((OWLObjectPropertyDomainAxiom) axiom).getProperty().equals(getRootObject());
    	}
    	return false;
    }
}
