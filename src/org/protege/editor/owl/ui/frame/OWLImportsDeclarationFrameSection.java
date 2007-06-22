package org.protege.editor.owl.ui.frame;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLOntology;

import java.util.Comparator;
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
 * Date: 24-Jan-2007<br><br>
 */
public class OWLImportsDeclarationFrameSection extends AbstractOWLFrameSection<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration> {

    private static final Logger logger = Logger.getLogger(OWLImportsDeclarationFrameSection.class);


    public static final String LABEL = "Direct imports";


    public OWLImportsDeclarationFrameSection(OWLEditorKit editorKit, OWLFrame<OWLOntology> frame) {
        super(editorKit, LABEL, frame);
    }


    public String getLabel() {
        return LABEL;
    }


    protected void clear() {
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        if (ontology.equals(getRootObject())) {
            for (OWLImportsDeclaration decl : ontology.getImportsDeclarations()) {
                addRow(new OWLImportsDeclarationAxiomFrameSectionRow(getOWLEditorKit(),
                                                                     this,
                                                                     ontology,
                                                                     getRootObject(),
                                                                     decl));
            }
        }
    }


    protected OWLImportsDeclaration createAxiom(OWLImportsDeclaration object) {
        return object;
    }


    public OWLFrameSectionRowObjectEditor<OWLImportsDeclaration> getObjectEditor() {
        return new OWLImportsDeclarationEditor(getOWLEditorKit());
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration>> getRowComparator() {
        return new Comparator<OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration>>() {
            public int compare(OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration> o1,
                               OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration> o2) {
                return o1.getAxiom().getImportedOntologyURI().compareTo(o2.getAxiom().getImportedOntologyURI());
            }
        };
    }


    public void visit(OWLImportsDeclaration axiom) {
        if (axiom.getSubject().equals(getRootObject())) {
            reset();
        }
    }
}
