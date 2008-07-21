package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.RemoveAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jul-2007<br><br>
 */
public class ConvertAssertionsOnPunsToAnnotations extends ProtegeOWLAction {
    private static Logger log = Logger.getLogger(ConvertAssertionsOnPunsToAnnotations.class);

    public void actionPerformed(ActionEvent e) {

        Set<OWLOntology> onts = getOWLModelManager().getOntologies();
        Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();

        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLOntology ont : onts) {
            individuals.addAll(ont.getReferencedIndividuals());
        }

        Set<OWLDataProperty> props = new HashSet<OWLDataProperty>();
        for (OWLIndividual ind : individuals) {
            boolean punned = false;
            for (OWLOntology ont : onts) {
                if (ont.containsClassReference(ind.getURI())) {
                    punned = true;
                    break;
                }
            }
            if (!punned) {
                continue;
            }

            for (OWLOntology ont : onts) {
                for (OWLDataPropertyAssertionAxiom ax : ont.getDataPropertyAssertionAxioms(ind)) {
                    if (!ax.getProperty().isAnonymous()) {
                        changes.add(new RemoveAxiom(ont, ax));
                        OWLDataFactory df = getOWLDataFactory();
                        OWLAnnotation anno = df.getOWLConstantAnnotation(((OWLDataProperty) ax.getProperty()).getURI(),
                                                                         ax.getObject());
                        OWLEntityAnnotationAxiom annoAx = df.getOWLEntityAnnotationAxiom(df.getOWLClass(ind.getURI()),
                                                                                         anno);
                        changes.add(new AddAxiom(ont, annoAx));
                        props.add((OWLDataProperty) ax.getProperty());
                    }
                }
            }
            for (OWLOntology ont : onts) {
                for (OWLAxiom ax : ont.getDeclarationAxioms(ind)) {
                    changes.add(new RemoveAxiom(ont, ax));
                }
                for (OWLClassAssertionAxiom ax : ont.getClassAssertionAxioms(ind)) {
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
        getOWLModelManager().applyChanges(changes);
        for (OWLOntology ont : onts) {
            for (OWLDataProperty prop : ont.getReferencedDataProperties()) {
                for (OWLOntology o : onts) {
                    for (OWLAxiom ax : o.getReferencingAxioms(prop)) {
                        log.info(ax);
                    }
                }
            }
        }
    }


    public void dispose() throws Exception {

    }


    public void initialise() throws Exception {

    }
}
