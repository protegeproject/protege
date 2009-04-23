package org.protege.editor.owl.ui.frame.individual;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.semanticweb.owl.model.OWLIndividual;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 30-Jan-2007<br><br>
 */
public class OWLIndividualPropertyAssertionsFrame<O extends OWLIndividual> extends AbstractOWLFrame<O> {

    public OWLIndividualPropertyAssertionsFrame(OWLEditorKit owlEditorKit) {
        super(owlEditorKit.getModelManager().getOWLOntologyManager());
        addSection(new OWLObjectPropertyAssertionAxiomFrameSection(owlEditorKit, this));
        addSection(new OWLDataPropertyAssertionAxiomFrameSection(owlEditorKit, this));
        addSection(new OWLNegativeObjectPropertyAssertionFrameSection(owlEditorKit, this));
        addSection(new OWLNegativeDataPropertyAssertionFrameSection(owlEditorKit, this));
    }
}
