package org.protege.editor.owl.ui.frame.annotationproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 4, 2009<br><br>
 */
public class OWLAnnotationPropertyDescriptionFrame extends AbstractOWLFrame<OWLAnnotationProperty> {

    public OWLAnnotationPropertyDescriptionFrame(OWLEditorKit owlEditorKit) {
        super(owlEditorKit.getModelManager().getOWLOntologyManager());
        addSection(new OWLAnnotationPropertyDomainFrameSection(owlEditorKit, this));
        addSection(new OWLAnnotationPropertyRangeFrameSection(owlEditorKit, this));
        addSection(new OWLSubAnnotationPropertyFrameSection(owlEditorKit, this));
    }
}
