package org.protege.editor.owl.ui.ontology.wizard.move.bydefinition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKit;
import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKitConfigurationPanel;
import org.protege.editor.owl.ui.ontology.wizard.move.byreference.SelectSignaturePanel;
import org.protege.editor.owl.ui.ontology.wizard.move.common.SignatureDependentSelectionPreviewPanel;
import org.protege.editor.owl.ui.ontology.wizard.move.common.SignatureSelection;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;

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
                        return ont.getAxioms(owlClass,Imports.EXCLUDED);
                    }

                    public Set<? extends OWLAxiom> visit(OWLObjectProperty property) {
                        return ont.getAxioms(property,Imports.EXCLUDED);
                    }

                    public Set<? extends OWLAxiom> visit(OWLDataProperty dataProperty) {
                        return ont.getAxioms(dataProperty,Imports.EXCLUDED);
                    }

                    public Set<? extends OWLAxiom> visit(OWLNamedIndividual individual) {
                        return ont.getAxioms(individual,Imports.EXCLUDED);
                    }

                    public Set<? extends OWLAxiom> visit(OWLDatatype owlDatatype) {
                        return ont.getAxioms(owlDatatype,Imports.EXCLUDED);
                    }

                    public Set<? extends OWLAxiom> visit(OWLAnnotationProperty property) {
                        return ont.getAxioms(property,Imports.EXCLUDED);
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
        selectSignaturePanel = new SelectSignaturePanel(this) {
            @Override
            public String getID() {
                return "MoveAxiomsByDefinition.Select.Signature";
            }
        };

        selectPreviewPanel = new SignatureDependentSelectionPreviewPanel(this) {
            @Override
            public String getID() {
                return "MoveAxiomsByDefinition.Signature.Preview";
            }
        };
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
