package org.protege.editor.owl.ui.frame;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLRuntimeException;
import org.semanticweb.owl.model.UnknownOWLOntologyException;

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
 * Date: 16-Feb-2007<br><br>
 */
public class OWLIndirectImportsFrameSection extends AbstractOWLFrameSection<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration> {

    private static final Logger logger = Logger.getLogger(OWLIndirectImportsFrameSection.class);


    public static final String LABEL = "Indirect imports";

    private Set<OWLImportsDeclaration> added = new HashSet<OWLImportsDeclaration>();


    public OWLIndirectImportsFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLOntology> frame) {
        super(editorKit, LABEL, frame);
    }


    protected OWLImportsDeclaration createAxiom(OWLImportsDeclaration object) {
        return object;
    }


    public OWLFrameSectionRowObjectEditor<OWLImportsDeclaration> getObjectEditor() {
        return null;
    }


    protected void clear() {
        added.clear();
    }


    public boolean canAddRows() {
        return false;
    }


    protected void refill(OWLOntology ontology) {
        try {
            for (OWLOntology ont : getOWLModelManager().getOWLOntologyManager().getImportsClosure(ontology)) {
                if (!ont.equals(ontology)) {
                    for (OWLImportsDeclaration dec : ont.getImportsDeclarations()) {
                        if (!added.contains(dec)) {
                            addRow(new OWLImportsDeclarationAxiomFrameSectionRow(getOWLEditorKit(),
                                                                                 this,
                                                                                 ont,
                                                                                 ontology,
                                                                                 dec));
                            added.add(dec);
                        }
                    }
                }
            }
        }
        catch (UnknownOWLOntologyException e) {
            // Programming error - convert to runtime exception
            throw new OWLRuntimeException(e);
        }
    }


    public Comparator<OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration>> getRowComparator() {
        return new Comparator<OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration>>() {
            public int compare(OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration> o1,
                               OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration> o2) {
                return o1.getAxiom().getImportedOntologyURI().compareTo(o2.getAxiom().getImportedOntologyURI());
            }
        };
    }


    public void visit(OWLImportsDeclaration axiom) {
        reset();
    }
}
