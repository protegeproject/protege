package org.protege.editor.owl.ui.ontology.wizard.move.bytype;

import org.protege.editor.owl.ui.ontology.wizard.move.FilteredAxiomsModel;
import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKit;
import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKitConfigurationPanel;
import org.protege.editor.owl.ui.ontology.wizard.move.SelectAxiomsPanel;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 28, 2008<br><br>
 */
public class MoveAxiomsByTypeKit extends MoveAxiomsKit implements FilteredAxiomsModel {

    private Set<AxiomType> types;

    private AxiomTypeSelectorPanel axiomTypeSelectorPanel;

    private SelectAxiomsPanel selectAxiomsPanel;

    private Set<OWLAxiom> axioms;


    public List<MoveAxiomsKitConfigurationPanel> getConfigurationPanels() {
        List<MoveAxiomsKitConfigurationPanel> panels = new ArrayList<>();
        panels.add(axiomTypeSelectorPanel);
        panels.add(selectAxiomsPanel);
        return panels;
    }


    public Set<OWLAxiom> getAxioms(Set<OWLOntology> sourceOntologies) {
        return axioms;
    }


    public void initialise() throws Exception {
        types = new HashSet<>();
        axiomTypeSelectorPanel = new AxiomTypeSelectorPanel(this);
        selectAxiomsPanel = new SelectAxiomsPanel(this, "axioms.type");
    }


    public void dispose() throws Exception {
        axiomTypeSelectorPanel.dispose();
    }


    public void setTypes(Set<AxiomType> types) {
        this.types.clear();
        this.types.addAll(types);
    }


    public Set<AxiomType> getTypes() {
        return types;
    }


    public void setFilteredAxioms(Set<OWLAxiom> axioms) {
        this.axioms = axioms;
    }


    public Set<OWLAxiom> getUnfilteredAxioms(Set<OWLOntology> sourceOntologies) {
        Set<OWLAxiom> axioms = new HashSet<>();
        for (OWLOntology ont : sourceOntologies){
            for (AxiomType type : types){
                axioms.addAll(ont.getAxioms(type));
            }
        }
        return axioms;
    }
}
