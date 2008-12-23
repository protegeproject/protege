package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLDescription;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 15-Feb-2007<br><br>
 *
 * @deprecated use <code>org.protege.editor.owl.ui.editor.OWLClassDescriptionEditor</code>
 */
public class OWLClassDescriptionEditor extends org.protege.editor.owl.ui.editor.OWLClassDescriptionEditor {

    public OWLClassDescriptionEditor(OWLEditorKit editorKit, OWLDescription description) {
        super(editorKit, description);
    }
}
