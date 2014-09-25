package org.protege.editor.owl.ui.frame.datatype;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.semanticweb.owlapi.model.OWLDatatype;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 5, 2009<br><br>
 */
public class OWLDatatypeDescriptionFrame extends AbstractOWLFrame<OWLDatatype> {

    public OWLDatatypeDescriptionFrame(OWLEditorKit owlEditorKit) {
        super(owlEditorKit.getModelManager().getOWLOntologyManager());
        addSection(new OWLDatatypeDefinitionFrameSection(owlEditorKit, this));
    }
}
