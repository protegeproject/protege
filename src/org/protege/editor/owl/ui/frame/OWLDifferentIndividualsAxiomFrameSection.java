package org.protege.editor.owl.ui.frame;

import java.util.Comparator;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;

import java.util.Comparator;
import java.util.HashSet;
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
 */

 /**
  * Author: Matthew Horridge<br>
  * The University Of Manchester<br>
  * Bio-Health Informatics Group<br>
  * Date: 29-Jan-2007<br><br>
  */
 public class OWLDifferentIndividualsAxiomFrameSection extends AbstractOWLFrameSection<OWLIndividual, OWLDifferentIndividualsAxiom, Set<OWLIndividual>> {

    public static final String LABEL = "Different individuals";

    private Set<OWLIndividual> added = new HashSet<OWLIndividual>();


    protected void clear() {
        added.clear();
    }


    public OWLDifferentIndividualsAxiomFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLIndividual> frame) {
        super(editorKit, LABEL, frame);
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLDifferentIndividualsAxiom ax : ontology.getDifferentIndividualAxioms(getRootObject())) {
            addRow(new OWLDifferentIndividualAxiomFrameSectionRow(getOWLEditorKit(),
                                                                  this,
                                                                  ontology,
                                                                  getRootObject(),
                                                                  ax));
            added.addAll(ax.getIndividuals());
        }
    }


    protected void refillInferred() throws OWLReasonerException {
    }


    protected OWLDifferentIndividualsAxiom createAxiom(Set<OWLIndividual> object) {
        object.add(getRootObject());
        return getOWLDataFactory().getOWLDifferentIndividualsAxiom(object);
    }


    public OWLFrameSectionRowObjectEditor<Set<OWLIndividual>> getObjectEditor() {
        return new OWLIndividualSetEditor(getOWLEditorKit());
    }


    public void visit(OWLDifferentIndividualsAxiom axiom) {
        if (axiom.getIndividuals().contains(getRootObject())) {
            reset();
        }
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLIndividual, OWLDifferentIndividualsAxiom, Set<OWLIndividual>>> getRowComparator() {
        return null;
    }
}
