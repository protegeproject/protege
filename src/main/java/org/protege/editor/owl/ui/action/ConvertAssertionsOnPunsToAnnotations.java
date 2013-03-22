package org.protege.editor.owl.ui.action;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jul-2007<br><br>
 */
public class ConvertAssertionsOnPunsToAnnotations extends ProtegeOWLAction {
    private static Logger log = Logger.getLogger(ConvertAssertionsOnPunsToAnnotations.class);

    public void actionPerformed(ActionEvent e) {

        OWLDataFactory df = getOWLDataFactory();
        Set<OWLOntology> onts = getOWLModelManager().getOntologies();
        Set<OWLIndividual> inds = new HashSet<OWLIndividual>();

        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLOntology ont : onts) {
            inds.addAll(ont.getIndividualsInSignature());
        }

        Set<OWLDataProperty> props = new HashSet<OWLDataProperty>();
        for (OWLIndividual ind : inds) {

            if (isPunForClass(ind)){
                OWLNamedIndividual pun = ind.asOWLNamedIndividual();
                for (OWLOntology ont : onts) {
                    for (OWLDataPropertyAssertionAxiom ax : ont.getDataPropertyAssertionAxioms(pun)) {
                        if (!ax.getProperty().isAnonymous()) {
                            changes.add(new RemoveAxiom(ont, ax));
                            OWLAnnotationProperty aProp = df.getOWLAnnotationProperty(ax.getProperty().asOWLDataProperty().getIRI());
                            OWLAnnotation anno = df.getOWLAnnotation(aProp, ax.getObject());
                            OWLAnnotationAssertionAxiom annoAx = df.getOWLAnnotationAssertionAxiom(pun.getIRI(), anno);
                            changes.add(new AddAxiom(ont, annoAx));
                            props.add((OWLDataProperty) ax.getProperty());
                        }
                    }
                }
                for (OWLOntology ont : onts) {
                    for (OWLAxiom ax : ont.getDeclarationAxioms(pun)) {
                        changes.add(new RemoveAxiom(ont, ax));
                    }
                    for (OWLClassAssertionAxiom ax : ont.getClassAssertionAxioms(pun)) {
                        changes.add(new RemoveAxiom(ont, ax));
                    }
                }
                for (OWLDataProperty prop : props) {
                    for (OWLOntology ont : onts) {
                        for (OWLAxiom ax : ont.getDeclarationAxioms(prop)) {
                            changes.add(new RemoveAxiom(ont, ax));
                        }
                        for (OWLAxiom ax : ont.getAxioms(prop)) {
                            changes.add(new RemoveAxiom(ont, ax));
                        }
                    }
                }

            }
        }
        getOWLModelManager().applyChanges(changes);
        for (OWLOntology ont : onts) {
            for (OWLDataProperty prop : ont.getDataPropertiesInSignature()) {
                for (OWLOntology o : onts) {
                    for (OWLAxiom ax : o.getReferencingAxioms(prop)) {
                        log.info(ax);
                    }
                }
            }
        }
    }


    private boolean isPunForClass(OWLIndividual ind) {
        if (ind.isAnonymous()){
            return false;
        }
        for (OWLOntology ont : getOWLModelManager().getOntologies()) {
            if (ont.containsClassInSignature(ind.asOWLNamedIndividual().getIRI())) {
                return true;
            }
        }
        return false;
    }


    public void dispose() throws Exception {

    }


    public void initialise() throws Exception {

    }
}
