package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLObject;

import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 25-Jan-2007<br><br>
 */
public interface OWLFrameObject<R extends Object, A extends OWLAxiom, E> {

    OWLFrameSectionRowObjectEditor<E> getEditor();


    boolean canAcceptDrop(List<OWLObject> objects);


    boolean dropObjects(List<OWLObject> objects);
}
