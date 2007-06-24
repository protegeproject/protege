package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Apr-2007<br><br>
 */
public class OWLGeneralClassAxiomsFrame extends AbstractOWLFrame<OWLOntology> {

    public OWLGeneralClassAxiomsFrame(OWLEditorKit owlEditorKit, OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
        addSection(new OWLGeneralClassAxiomsFrameSection(owlEditorKit, this));
    }
}
