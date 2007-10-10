package org.protege.editor.owl.ui.frame;

import java.util.List;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLObject;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 25-Jan-2007<br><br>
 */
public interface OWLFrameObject<R extends OWLObject, A extends OWLAxiom, E> {

    OWLFrameSectionRowObjectEditor<E> getEditor();


    boolean canAcceptDrop(List<OWLObject> objects);


    boolean dropObjects(List<OWLObject> objects);
}
