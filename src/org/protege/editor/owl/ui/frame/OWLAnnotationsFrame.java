package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 26-Jan-2007<br><br>
 */
public class OWLAnnotationsFrame extends AbstractOWLFrame<OWLAnnotationSubject> {

    public OWLAnnotationsFrame(OWLEditorKit man) {
        super(man.getModelManager().getOWLOntologyManager());
        addSection(new OWLAnnotationFrameSection(man, this));
    }
}
