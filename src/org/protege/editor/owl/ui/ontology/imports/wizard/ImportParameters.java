package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.net.URI;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 13-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface ImportParameters {

    public Set<URI> getOntologiesToBeImported();


    public String getOntologyLocationDescription(URI ontologyURI);


    public void performImportSetup(OWLEditorKit editorKit);
}
