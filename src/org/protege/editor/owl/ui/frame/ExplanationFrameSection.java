package org.protege.editor.owl.ui.frame;

import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;
import org.semanticweb.owl.util.OWLEntityCollector;
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
 * Date: 15-Oct-2007<br><br>
 */
public class ExplanationFrameSection extends AbstractOWLFrameSection<OWLAxiom, OWLAxiom, OWLAxiom>{

    private int explanationNumber;

    private Set<OWLAxiom> axioms;

    private OWLAxiom entailedAxiom;

    private boolean addedEntailedAxiom;

    private Set<OWLAxiom> added;

    public ExplanationFrameSection(OWLEditorKit editorKit, int explanationNumber, OWLAxiom entailedAxiom, Set<OWLAxiom> axioms,  OWLFrame<? extends OWLAxiom> owlFrame) {
        super(editorKit, "Explanation " + explanationNumber, owlFrame);
        this.axioms = new HashSet<OWLAxiom>(axioms);
        this.entailedAxiom = entailedAxiom;
        added = new HashSet<OWLAxiom>();
    }


    public int getExplanationNumber() {
        return explanationNumber;
    }


    protected void clear() {
        addedEntailedAxiom = false;
        added.clear();
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
        if(!addedEntailedAxiom) {
            addRow(new ExplanationFrameSectionRow(getOWLEditorKit(), this, null, getRootObject(), entailedAxiom));
            addedEntailedAxiom = true;
        }
        for(OWLAxiom ax : axioms) {

            if(!added.contains(ax)) {
                added.add(ax);
                addRow(new ExplanationFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
            }
        }
    }


    public Comparator<OWLFrameSectionRow<OWLAxiom, OWLAxiom, OWLAxiom>> getRowComparator() {
        return null;
    }


    private void orderAxioms() {

        Map<OWLEntity, Set<OWLAxiom>> lhs2AxiomMap = new HashMap<OWLEntity, Set<OWLAxiom>>();
        for(OWLFrameSectionRow row : getRows()) {
            OWLAxiom ax = row.getAxiom();
        }
    }


    private class OrderingVisitor extends OWLAxiomVisitorAdapter {

        private Map<OWLEntity, Set<OWLAxiom>> orderingMap;


        public OrderingVisitor(Map<OWLEntity, Set<OWLAxiom>> orderingMap) {
            this.orderingMap = orderingMap;
        }

        private Set<OWLAxiom> getAxioms(OWLEntity ent) {
            Set<OWLAxiom> axs = orderingMap.get(ent);
            if(axs == null) {
                axs = new HashSet<OWLAxiom>();
                orderingMap.put(ent, axs);
            }
            return axs;
        }


        public void visit(OWLSubClassAxiom owlSubClassAxiom) {
            if(!owlSubClassAxiom.getSubClass().isAnonymous()) {
                OWLClass lhs = owlSubClassAxiom.getSubClass().asOWLClass();
                getAxioms(lhs).add(owlSubClassAxiom);
            }
        }

        
    }
}
