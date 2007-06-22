package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.*;

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
 * Date: 23-Feb-2007<br><br>
 */
public class InheritedAnonymousClassesFrameSection extends AbstractOWLFrameSection<OWLClass, OWLClassAxiom, OWLDescription> {

    private static final String LABEL = "Inherited anonymous classes";

    private Set<OWLClass> processedClasses = new HashSet<OWLClass>();


    public InheritedAnonymousClassesFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLClass> frame) {
        super(editorKit, LABEL, frame);
    }


    protected OWLSubClassAxiom createAxiom(OWLDescription object) {
        return getOWLDataFactory().getOWLSubClassAxiom(getRootObject(), object);
    }


    public OWLFrameSectionRowObjectEditor<OWLDescription> getObjectEditor() {
        return new OWLClassDescriptionEditor(getOWLEditorKit(), null);
    }


    protected void refill(OWLOntology ontology) {
        Set<OWLClass> clses = getOWLModelManager().getOWLClassHierarchyProvider().getAncestors(getRootObject());
        clses.remove(getRootObject());
        for (OWLClass cls : clses) {
            for (OWLSubClassAxiom ax : ontology.getSubClassAxiomsForLHS(cls)) {
                if (ax.getSuperClass().isAnonymous()) {
                    addRow(new InheritedAnonymousClassesFrameSectionRow(getOWLEditorKit(), this, ontology, cls, ax));
                }
            }
            for (OWLEquivalentClassesAxiom ax : ontology.getEquivalentClassesAxioms(cls)) {
                Set<OWLDescription> descs = new HashSet<OWLDescription>(ax.getDescriptions());
                descs.remove(getRootObject());
                addRow(new InheritedAnonymousClassesFrameSectionRow(getOWLEditorKit(),
                                                                    this,
                                                                    ontology,
                                                                    cls,
                                                                    getOWLDataFactory().getOWLSubClassAxiom(
                                                                            getRootObject(),
                                                                            descs.iterator().next())));
            }
            processedClasses.add(cls);
        }
    }


    protected void refillInferred() {
        try {
            if (!getOWLModelManager().getReasoner().isSatisfiable(getRootObject())) {
                return;
            }
            Set<OWLClass> clses = OWLReasonerAdapter.flattenSetOfSets(getOWLModelManager().getReasoner().getAncestorClasses(
                    getRootObject()));
            clses.remove(getRootObject());
            for (OWLClass cls : clses) {
                if (!processedClasses.contains(cls)) {
                    for (OWLOntology ontology : getOWLModelManager().getActiveOntologies()) {
                        for (OWLSubClassAxiom ax : ontology.getSubClassAxiomsForLHS(cls)) {
                            if (ax.getSuperClass().isAnonymous()) {
                                addRow(new InheritedAnonymousClassesFrameSectionRow(getOWLEditorKit(),
                                                                                    this,
                                                                                    null,
                                                                                    cls,
                                                                                    ax));
                            }
                        }
                        for (OWLEquivalentClassesAxiom ax : ontology.getEquivalentClassesAxioms(cls)) {
                            Set<OWLDescription> descs = new HashSet<OWLDescription>(ax.getDescriptions());
                            descs.remove(getRootObject());
                            OWLDescription superCls = descs.iterator().next();
                            if (superCls.isAnonymous()) {
                                addRow(new InheritedAnonymousClassesFrameSectionRow(getOWLEditorKit(),
                                                                                    this,
                                                                                    null,
                                                                                    cls,
                                                                                    getOWLDataFactory().getOWLSubClassAxiom(
                                                                                            getRootObject(),
                                                                                            superCls)));
                            }
                        }
                    }
                }
            }
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public boolean canAddRows() {
        return false;
    }


    public void visit(OWLSubClassAxiom axiom) {
        reset();
    }


    public void visit(OWLEquivalentClassesAxiom axiom) {
        reset();
    }


    protected void clear() {
        processedClasses.clear();
    }


    public Comparator<OWLFrameSectionRow<OWLClass, OWLClassAxiom, OWLDescription>> getRowComparator() {
        return null;
    }
}
