package org.protege.editor.owl.ui.frame;

import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 15-Feb-2007<br><br>
 */
public interface OWLFrameSectionRowObjectEditorHandler<O> {

    void handleEditingFinished(Set<O> editedObjects);
}
