package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;

import java.util.Set;


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

    public Set<IRI> getOntologiesToBeImported();


    public String getOntologyLocationDescription(IRI ontologyIRI);


    public void performImportSetup(OWLEditorKit editorKit);
}
