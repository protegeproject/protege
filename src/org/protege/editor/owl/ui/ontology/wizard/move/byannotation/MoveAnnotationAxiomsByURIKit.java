package org.protege.editor.owl.ui.ontology.wizard.move.byannotation;

import org.protege.editor.owl.ui.ontology.wizard.move.FilteredAxiomsModel;
import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKit;
import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKitConfigurationPanel;
import org.protege.editor.owl.ui.ontology.wizard.move.SelectAxiomsPanel;
import org.semanticweb.owl.model.AxiomType;
import org.semanticweb.owl.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.net.URI;
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
 * Date: Nov 28, 2008<br><br>
 */
public class MoveAnnotationAxiomsByURIKit extends MoveAxiomsKit implements FilteredAxiomsModel {

    private Set<URI> uris;

    private AnnotationSelectorPanel annotationURISelectorPanel;

    private SelectAxiomsPanel selectAxiomsPanel;

    private Set<OWLAxiom> filteredAxioms;


    public List<MoveAxiomsKitConfigurationPanel> getConfigurationPanels() {
        List<MoveAxiomsKitConfigurationPanel> panels = new ArrayList<MoveAxiomsKitConfigurationPanel>();
        panels.add(annotationURISelectorPanel);
        panels.add(selectAxiomsPanel);
        return panels;
    }


    public Set<OWLAxiom> getAxioms(Set<OWLOntology> sourceOntologies) {
        return filteredAxioms;
    }


    public void initialise() throws Exception {
        uris = new HashSet<URI>();
        annotationURISelectorPanel = new AnnotationSelectorPanel(this);
        selectAxiomsPanel = new SelectAxiomsPanel(this, "axioms.annotate");
    }


    public void dispose() throws Exception {
        annotationURISelectorPanel.dispose();
    }


    public void setURIs(Set<URI> uris) {
        this.uris.clear();
        this.uris.addAll(uris);
    }


    public Set<URI> getTypes() {
        return uris;
    }


    public void setFilteredAxioms(Set<OWLAxiom> axioms) {
        this.filteredAxioms = axioms;
    }


    public Set<OWLAxiom> getUnfilteredAxioms(Set<OWLOntology> sourceOntologies) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLOntology ont : sourceOntologies){
            for (OWLAnnotationAssertionAxiom ax : ont.getAxioms(AxiomType.ANNOTATION_ASSERTION)){
                if (uris.contains(ax.getAnnotation().getProperty().getURI())){
                    axioms.add(ax);
                }
            }
        }
        return axioms;
    }
}