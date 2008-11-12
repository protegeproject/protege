package org.protege.editor.owl.ui.renderer;

import org.semanticweb.owl.model.OWLObject;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 2, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLObjectRenderer {

    /**
     * @param object - the object to render
     * @param entityRenderer optional - most implementations will be able to get the default entity rendering from the OWLModelManager
     * @return a string rendering of the given object
     */
    public String render(OWLObject object, OWLEntityRenderer entityRenderer);

    public void setFocusedObject(OWLObject object);
}
