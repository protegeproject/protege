package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLIndividual;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLIndividualFrame extends AbstractOWLFrame<OWLIndividual> {

    public OWLIndividualFrame(OWLEditorKit editorKit) {
        super(editorKit.getOWLModelManager().getOWLOntologyManager());
        addSection(new OWLClassAssertionAxiomTypeFrameSection(editorKit, this));
        addSection(new OWLSameIndividualsAxiomFrameSection(editorKit, this));
        addSection(new OWLDifferentIndividualsAxiomFrameSection(editorKit, this));
    }
}
