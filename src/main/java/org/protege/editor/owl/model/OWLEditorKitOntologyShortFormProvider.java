package org.protege.editor.owl.model;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 28/09/2012
 */
public class OWLEditorKitOntologyShortFormProvider extends OntologyIRIShortFormProvider {

    private OWLEditorKit editorKit;

    public OWLEditorKitOntologyShortFormProvider(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
    }


}
