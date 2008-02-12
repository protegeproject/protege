package org.protege.editor.owl.ui.frame;

import org.semanticweb.owl.model.*;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.protege.editor.owl.OWLEditorKit;

import java.util.*;
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
 * Date: 19-Dec-2007<br><br>
 */
public class AxiomListFrameSection extends AbstractOWLFrameSection<Set<OWLAxiom>, OWLAxiom, OWLAxiom> {

    private RowComparator rowComparator = new RowComparator();

    private Set<OWLAxiom> added;

    public AxiomListFrameSection(OWLEditorKit editorKit, OWLFrame<Set<OWLAxiom>> owlFrame) {
        super(editorKit, "Axioms", owlFrame);
        added = new HashSet<OWLAxiom>();
    }


    protected OWLAxiom createAxiom(OWLAxiom object) {
        return null;
    }


    public OWLFrameSectionRowObjectEditor<OWLAxiom> getObjectEditor() {
        return null;
    }


    protected void refill(OWLOntology ontology) {
        Set<OWLAxiom> axs = getRootObject();
        for(OWLAxiom ax : axs) {
            if (ontology.containsAxiom(ax)) {
                addRow(new AxiomListFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
            }
        }
    }


    protected void refillInferred() throws OWLReasonerException {
        Set<OWLAxiom> axs = getRootObject();
        for(OWLAxiom ax : axs) {
            if (!added.contains(ax)) {
                addRow(new AxiomListFrameSectionRow(getOWLEditorKit(), this, null, getRootObject(), ax));
            }
        }
    }


    protected void clear() {
        added.clear();
    }


    public Comparator<OWLFrameSectionRow<Set<OWLAxiom>, OWLAxiom, OWLAxiom>> getRowComparator() {

        return rowComparator;
    }


    public boolean canAdd() {
        return false;
    }


    protected void handleChanges(List<? extends OWLOntologyChange> changes) {
        reset();
    }

    private class RowComparator implements Comparator<OWLFrameSectionRow<Set<OWLAxiom>, OWLAxiom, OWLAxiom>> {


        public int compare(OWLFrameSectionRow<Set<OWLAxiom>, OWLAxiom, OWLAxiom> o1,
                           OWLFrameSectionRow<Set<OWLAxiom>, OWLAxiom, OWLAxiom> o2) {
            String ren1 = getOWLModelManager().getRendering(o1.getAxiom());
            String ren2 = getOWLModelManager().getRendering(o2.getAxiom());
            if (ren1.length() != 0) {
                return ren1.compareTo(ren2);
            }
            else {
                return 1;
            }
        }
    }
}
