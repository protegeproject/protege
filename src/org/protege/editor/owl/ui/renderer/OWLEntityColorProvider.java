package org.protege.editor.owl.ui.renderer;

import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLEntity;

import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class OWLEntityColorProvider implements ProtegePluginInstance {

    private OWLModelManager owlModelManager;


    protected void setup(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }


    public OWLModelManager getOWLModelManager() {
        return owlModelManager;
    }


    /**
     * Gets the color that should be used to paint the
     * specified entity name.
     * @param entity The entity whose name will be painted
     * @return The <code>Color</code> that is used to paint
     *         the entity, or <code>null</code> if the default color
     *         should be used.
     */
    public abstract Color getColor(OWLEntity entity);
}
