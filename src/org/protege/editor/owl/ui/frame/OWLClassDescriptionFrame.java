package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLClass;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public class OWLClassDescriptionFrame extends AbstractOWLFrame<OWLClass> {

    public OWLClassDescriptionFrame(OWLEditorKit editorKit) {
        super(editorKit.getOWLModelManager().getOWLOntologyManager());
//        addSection(new OWLAnnotationFrameSection(editorKit, this));
        addSection(new OWLEquivalentClassesAxiomFrameSection(editorKit, this));
        addSection(new OWLSubClassAxiomFrameSection(editorKit, this));
        addSection(new InheritedAnonymousClassesFrameSection(editorKit, this));
        addSection(new OWLClassAssertionAxiomIndividualSection(editorKit, this));
        addSection(new OWLDisjointClassesAxiomFrameSection(editorKit, this));
    }
}
