package org.protege.editor.owl.ui.action;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31 Aug 16
 */
public class MakeInstancesOfClassDifferentIndividualsAction extends SelectedOWLClassAction {

    @Override
    protected void initialiseAction() throws Exception {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OWLClass selectedClass = getOWLClass();
        if(selectedClass == null) {
            return;
        }

        Set<OWLNamedIndividual> individuals = getOWLModelManager().getActiveOntologies().stream()
                .flatMap(o -> o.getClassAssertionAxioms(selectedClass).stream())
                .map(OWLClassAssertionAxiom::getIndividual)
                .filter(i -> !i.isAnonymous())
                .map(OWLIndividual::asOWLNamedIndividual)
                .collect(toSet());


        OWLOntology activeOntology = getOWLModelManager().getActiveOntology();

        List<OWLOntologyChange> removeExistingAxiomsChanges = activeOntology
                .getAxioms(AxiomType.DIFFERENT_INDIVIDUALS)
                .stream()
                .filter(ax -> individuals.containsAll(ax.getIndividuals()))
                .map(ax -> new RemoveAxiom(activeOntology, ax))
                .collect(toList());

        List<OWLOntologyChange> allChanges = new ArrayList<>();
        if(!removeExistingAxiomsChanges.isEmpty()) {
            int ret = JOptionPane.showConfirmDialog(
                    getWorkspace(),
                    "Do you want to remove existing Different Individuals axioms which assert that\n" +
                            "some instances of the selected class are different?",
                    "Remove existing axioms", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(ret == JOptionPane.YES_OPTION) {
                allChanges.addAll(removeExistingAxiomsChanges);
            }
        }
        OWLDifferentIndividualsAxiom ax = getOWLDataFactory().getOWLDifferentIndividualsAxiom(individuals);
        allChanges.add(new AddAxiom(activeOntology, ax));
        getOWLModelManager().applyChanges(allChanges);
    }
}
