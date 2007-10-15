package org.protege.editor.owl.ui.frame;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.protege.editor.owl.OWLEditorKit;

import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;
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
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 15-Oct-2007<br><br>
 */
public class ExplanationFrameSection extends AbstractOWLFrameSection<OWLAxiom, OWLAxiom, OWLAxiom>{

    private int explanationNumber;

    private Set<OWLAxiom> axioms;

    public ExplanationFrameSection(OWLEditorKit editorKit, int explanationNumber, Set<OWLAxiom> axioms,  OWLFrame<? extends OWLAxiom> owlFrame) {
        super(editorKit, "Explanation " + explanationNumber, owlFrame);
        this.axioms = new HashSet<OWLAxiom>(axioms);
    }


    public int getExplanationNumber() {
        return explanationNumber;
    }


    protected void clear() {

    }


    public boolean canAddRows() {
        return false;
    }


    protected OWLAxiom createAxiom(OWLAxiom object) {
        return object;
    }


    public OWLFrameSectionRowObjectEditor<OWLAxiom> getObjectEditor() {
        return null;
    }


    protected void refill(OWLOntology ontology) {
        for(OWLAxiom ax : axioms) {
            if(ontology.containsAxiom(ax)) {
                addRow(new ExplanationFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
            }
        }
    }


    public Comparator<OWLFrameSectionRow<OWLAxiom, OWLAxiom, OWLAxiom>> getRowComparator() {
        return null;
    }
}
