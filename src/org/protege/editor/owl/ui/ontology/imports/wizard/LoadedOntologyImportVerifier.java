package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LoadedOntologyImportVerifier implements ImportVerifier {

    private static final Logger logger = Logger.getLogger(LoadedOntologyImportVerifier.class);

    private Set<OWLOntology> ontologies;


    public LoadedOntologyImportVerifier(Set<OWLOntology> ontologies) {
        this.ontologies = ontologies;
    }


    public ImportParameters checkImports() {
        return new ImportParameters() {
            public Set<URI> getOntologiesToBeImported() {
                Set<URI> uris = new HashSet<URI>();
                for (OWLOntology ont : ontologies) {
                    uris.add(ont.getURI());
                }
                return uris;
            }


            public String getOntologyLocationDescription(URI ontologyURI) {
                return "Already loaded";
            }


            public void performImportSetup(OWLEditorKit editorKit) {
                // Nothing to do
            }
        };
    }
}
