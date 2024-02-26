package org.protege.editor.owl.ui.frame;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Dec-2007<br><br>
 */
public class AxiomListFrameSection extends AbstractOWLFrameSection<Set<OWLAxiom>, OWLAxiom, OWLAxiom> {

    private RowComparator rowComparator;

    private Set<OWLAxiom> added;

    public AxiomListFrameSection(OWLEditorKit editorKit, OWLFrame<Set<OWLAxiom>> owlFrame) {
        super(editorKit, "Axioms", "Axiom", owlFrame);
        rowComparator = new RowComparator();
        added = new HashSet<>();
    }


    protected OWLAxiom createAxiom(OWLAxiom object) {
        return null;
    }


    public OWLObjectEditor<OWLAxiom> getObjectEditor() {
        return null;
    }


    protected void refill(OWLOntology ontology) {
        Set<OWLAxiom> axs = getRootObject();
        for(OWLAxiom ax : axs) {
            if (ontology.containsAxiom(ax)) {
                addRow(new AxiomListFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
                added.add(ax);
            }
        }
    }


    protected void refillInferred() {
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

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	return change.isAxiomChange();
    }

    private class RowComparator implements Comparator<OWLFrameSectionRow<Set<OWLAxiom>, OWLAxiom, OWLAxiom>> {

        private Comparator<OWLObject> objComparator;

        public RowComparator(){
            this.objComparator = getOWLModelManager().getOWLObjectComparator();
        }

        public int compare(OWLFrameSectionRow<Set<OWLAxiom>, OWLAxiom, OWLAxiom> o1,
                           OWLFrameSectionRow<Set<OWLAxiom>, OWLAxiom, OWLAxiom> o2) {
            return objComparator.compare(o1.getAxiom(), o2.getAxiom());
        }
    }
}
