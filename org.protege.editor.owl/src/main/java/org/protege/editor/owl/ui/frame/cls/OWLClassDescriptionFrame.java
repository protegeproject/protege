package org.protege.editor.owl.ui.frame.cls;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.semanticweb.owlapi.model.OWLClass;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public class OWLClassDescriptionFrame extends AbstractOWLFrame<OWLClass> {

    public OWLClassDescriptionFrame(OWLEditorKit editorKit) {
        super(editorKit.getModelManager().getOWLOntologyManager());
        addSection(new OWLEquivalentClassesAxiomFrameSection(editorKit, this));
        addSection(new OWLSubClassAxiomFrameSection(editorKit, this));
        addSection(new InheritedAnonymousClassesFrameSection(editorKit, this));
        addSection(new OWLClassAssertionAxiomMembersSection(editorKit, this));
        addSection(new OWLKeySection(editorKit, this));
        addSection(new OWLDisjointClassesAxiomFrameSection(editorKit, this));
        addSection(new OWLDisjointUnionAxiomFrameSection(editorKit, this));
    }
}
