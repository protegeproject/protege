package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.library.OntologyLibrary;

import java.net.URI;
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
 * Medical Informatics Group<br>
 * Date: 13-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LibraryVerifier implements ImportVerifier {

    private OWLModelManager owlModelManager;

    private Set<URI> uris;


    public LibraryVerifier(OWLModelManager owlModelManager, Set<URI> uris) {
        this.owlModelManager = owlModelManager;
        this.uris = new HashSet<URI>(uris);
    }


    public ImportParameters checkImports() {
//        // All should be o.k.
        return new ImportParameters() {
            public Set<URI> getOntologiesToBeImported() {
                return uris;
            }


            public String getOntologyLocationDescription(URI ontologyURI) {
                OntologyLibrary lib = owlModelManager.getOntologyLibraryManager().getLibrary(ontologyURI);
                return lib.getPhysicalURI(ontologyURI).toString();
            }


            public void performImportSetup(OWLEditorKit editorKit) {
                // Nothing to do
            }
        };
    }
}
