package org.protege.editor.owl.ui.ontology.wizard.move.byprofile;

import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKit;
import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKitConfigurationPanel;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Dec 3, 2008<br><br>
 */
public class MoveAxiomsByProfileKit extends MoveAxiomsKit {

    private ProfileSelectorPanel profileSelectorPanel;

    private OWLProfile profile;


    public List<MoveAxiomsKitConfigurationPanel> getConfigurationPanels() {
        List<MoveAxiomsKitConfigurationPanel> panels = new ArrayList<MoveAxiomsKitConfigurationPanel>();
        panels.add(profileSelectorPanel);
        return panels;
    }


    public Set<OWLAxiom> getAxioms(Set<OWLOntology> sourceOntologies) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
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
