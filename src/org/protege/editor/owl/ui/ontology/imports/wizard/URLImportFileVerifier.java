package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.repository.OntologyURIExtractor;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLRuntimeException;
import org.semanticweb.owl.util.SimpleURIMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
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
public class URLImportFileVerifier implements ImportVerifier {

    private URL url;


    public URLImportFileVerifier(URL url) {
        this.url = url;
    }


    public ImportParameters checkImports() {
        try {
            OntologyURIExtractor extractor = new OntologyURIExtractor(url.toURI());
            final URI ontologyURI = extractor.getOntologyURI();
            if (extractor.isStartElementPresent() == false || ontologyURI == null) {
                String msg = "The ontology contained in the document located at " + url.toString() + " could " + "not be imported. ";
                if (extractor.isStartElementPresent() == false) {
                    msg += " The document does not appear to contain a valid RDF/XML representation" + "of an ontology.";
                }
                throw new RuntimeException(msg);
            }
            else {
                return new ImportParameters() {
                    public Set<URI> getOntologiesToBeImported() {
                        return Collections.singleton(ontologyURI);
                    }


                    public String getOntologyLocationDescription(URI ontologyURI) {
                        return url.toString();
                    }


                    public void performImportSetup(OWLEditorKit editorKit) {
                        // May be we need to add a mapping?
                        try {
                            OWLOntologyManager man = editorKit.getOWLModelManager().getOWLOntologyManager();
                            man.addURIMapper(new SimpleURIMapper(ontologyURI, url.toURI()));
                        }
                        catch (URISyntaxException e) {
                            throw new OWLRuntimeException(e);
                        }
                    }
                };
            }
        }
        catch (URISyntaxException e) {
            throw new OWLRuntimeException(e);
        }
    }
}
