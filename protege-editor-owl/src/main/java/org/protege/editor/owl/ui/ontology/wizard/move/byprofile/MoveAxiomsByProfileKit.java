package org.protege.editor.owl.ui.ontology.wizard.move.byprofile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKit;
import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKitConfigurationPanel;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Dec 3, 2008<br><br>
 */
public class MoveAxiomsByProfileKit extends MoveAxiomsKit {

    private ProfileSelectorPanel profileSelectorPanel;

    private OWLProfile profile;


    public List<MoveAxiomsKitConfigurationPanel> getConfigurationPanels() {
        List<MoveAxiomsKitConfigurationPanel> panels = new ArrayList<>();
        panels.add(profileSelectorPanel);
        return panels;
    }


    public Set<OWLAxiom> getAxioms(Set<OWLOntology> sourceOntologies) {
        Set<OWLAxiom> axioms = new HashSet<>();
        for (OWLOntology ont: sourceOntologies){
            axioms.addAll(ont.getAxioms());
        }
        for (OWLOntology ont : sourceOntologies){
            OWLProfileReport report = profile.checkOntology(ont);
            for (OWLProfileViolation disConstr : report.getViolations()){
                axioms.remove(disConstr.getAxiom());
            }
        }
        return axioms;
    }


    public void initialise() throws Exception {
        profileSelectorPanel = new ProfileSelectorPanel(this);
    }


    public void dispose() throws Exception {
        profileSelectorPanel.dispose();
    }


    public void setProfile(OWLProfile profile){
        this.profile = profile;
    }
}
