package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Jan-2007<br><br>
 */
public class OWLImportsDeclarationsFrame extends AbstractOWLFrame<OWLOntology> {

    public OWLImportsDeclarationsFrame(OWLEditorKit editorKit) {
        super(editorKit.getModelManager().getOWLOntologyManager());
        addSection(new OWLImportsDeclarationFrameSection(editorKit, this));
        addSection(new OWLIndirectImportsFrameSection(editorKit, this));
    }
}
