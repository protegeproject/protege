package org.protege.editor.owl.ui.ontology.wizard.move.bydefinition;

import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKit;
import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKitConfigurationPanel;
import org.protege.editor.owl.ui.ontology.wizard.move.byreference.SelectSignaturePanel;
import org.protege.editor.owl.ui.ontology.wizard.move.common.SignatureDependentSelectionPreviewPanel;
import org.protege.editor.owl.ui.ontology.wizard.move.common.SignatureSelection;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 19/09/2012
 */
public class MoveAxiomsByDefinitionKit extends MoveAxiomsKit implements SignatureSelection {

    private Set<OWLEntity> selectedEntities;

    private SelectSignaturePanel selectSignaturePanel;

    private SignatureDependentSelectionPreviewPanel selectPreviewPanel;


    public List<MoveAxiomsKitConfigurationPanel> getConfigurationPanels() {
        List<MoveAxiomsKitConfigurationPanel> panels = new ArrayList<MoveAxiomsKitConfigurationPanel>();
        panels.add(selectSignaturePanel);
        panels.add(selectPreviewPanel);
        return panels;
    }


    public Set<OWLAxiom> getAxioms(Set<OWLOntology> sourceOntologies) {
        return getAxioms(sourceOntologies, selectedEntities);
    }


    public Set<OWLAxiom> getAxioms(Set<OWLOntology> ontologies, Set<OWLEntity> entities) {
        Set<OWLAxiom> result = new HashSet<OWLAxiom>();
        for (OWLEntity e : entities) {
            for(final OWLOntology ont : ontologies) {
                Set<? extends OWLAxiom> axioms = e.accept(new OWLEntityVisitorEx<Set<? extends OWLAxiom>>() {
                    public Set<? extends OWLAxiom> visit(OWLClass owlClass) {
                        return ont.getAxioms(owlClass);
                    }

                    public Set<? extends OWLAxiom> visit(OWLObjectProperty property) {
                        return ont.getAxioms(property);
                    }

                    public Set<? extends OWLAxiom> visit(OWLDataProperty dataProperty) {
                        return ont.getAxioms(dataProperty);
                    }

                    public Set<? extends OWLAxiom> visit(OWLNamedIndividual individual) {
                        return ont.getAxioms(individual);
                    }

                    public Set<? extends OWLAxiom> visit(OWLDatatype owlDatatype) {
                        return ont.getAxioms(owlDatatype);
                    }

                    public Set<? extends OWLAxiom> visit(OWLAnnotationProperty property) {
                        return ont.getAxioms(property);
                    }
                });
                result.addAll(ont.getDeclarationAxioms(e));
                result.addAll(axioms);
                result.addAll(ont.getAnnotationAssertionAxioms(e.getIRI()));
            }
        }
        return result;
    }


    public void initialise() throws Exception {
        selectedEntities = new HashSet<OWLEntity>();
        selectSignaturePanel = new SelectSignaturePanel(this);

        selectPreviewPanel = new SignatureDependentSelectionPreviewPanel(this);
    }


    public void dispose() throws Exception {
    }


    public Set<OWLEntity> getSignature() {
        return selectedEntities;
    }


    public void setSignature(Set<OWLEntity> entities) {
        selectedEntities.clear();
        selectedEntities.addAll(entities);
    }
    
}
