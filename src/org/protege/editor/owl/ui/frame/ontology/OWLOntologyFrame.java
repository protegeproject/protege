package org.protege.editor.owl.ui.frame.ontology;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.protege.editor.owl.ui.frame.InferredAxiomsFrameSection;
import org.semanticweb.owlapi.model.OWLOntology;
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
 * Date: 29-Oct-2007<br><br>
 */
public class OWLOntologyFrame extends AbstractOWLFrame<OWLOntology> {


    public OWLOntologyFrame(OWLEditorKit editorKit) {
        super(editorKit.getModelManager().getOWLOntologyManager());
// @@TODO v3 port
//        addSection(new OWLOntologyAnnotationAxiomFrameSection(editorKit, this));
//        addSection(new OWLImportsDeclarationFrameSection(editorKit, this));
//        addSection(new OWLIndirectImportsFrameSection(editorKit, this));
        addSection(new InferredAxiomsFrameSection(editorKit, this));
    }
}
