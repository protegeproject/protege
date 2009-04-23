package org.protege.editor.owl.ui.frame.editor;

import java.util.Collections;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 15-Feb-2007<br><br>
 */
public abstract class AbstractOWLFrameSectionRowObjectEditor<E> implements OWLFrameSectionRowObjectEditor<E> {

    private OWLFrameSectionRowObjectEditorHandler<E> handler;


    public void setHandler(OWLFrameSectionRowObjectEditorHandler<E> handler) {
        this.handler = handler;
    }


    public OWLFrameSectionRowObjectEditorHandler<E> getHandler() {
        return handler;
    }


    public boolean isMultiEditSupported() {
        return false;
    }


    public Set<E> getEditedObjects() {
        final E object = getEditedObject();
        if (object != null) {
            return Collections.singleton(object);
        }
        else {
            return Collections.emptySet();
        }
    }
}
