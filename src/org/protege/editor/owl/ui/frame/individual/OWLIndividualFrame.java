package org.protege.editor.owl.ui.frame.individual;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.semanticweb.owl.model.OWLNamedIndividual;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLIndividualFrame extends AbstractOWLFrame<OWLNamedIndividual> {

    public OWLIndividualFrame(OWLEditorKit editorKit) {
        super(editorKit.getModelManager().getOWLOntologyManager());
        addSection(new OWLClassAssertionAxiomTypeFrameSection(editorKit, this));
        addSection(new OWLSameIndividualsAxiomFrameSection(editorKit, this));
        addSection(new OWLDifferentIndividualsAxiomFrameSection(editorKit, this));
    }
}
