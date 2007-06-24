package org.protege.editor.owl.ui.renderer;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.OWLEntity;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 30-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractOWLEntityRenderer implements OWLModelManagerEntityRenderer {

    private static final Logger logger = Logger.getLogger(AbstractOWLEntityRenderer.class);


    private List<OWLEntityRendererListener> listeners = new ArrayList<OWLEntityRendererListener>();


    public void addListener(OWLEntityRendererListener listener) {
        listeners.add(listener);
    }


    public void removeListener(OWLEntityRendererListener listener) {
        listeners.remove(listener);
    }


    protected void fireRenderingChanged(OWLEntity entity) {
        for (OWLEntityRendererListener listener : new ArrayList<OWLEntityRendererListener>(listeners)) {
            listener.renderingChanged(entity, this);
        }
    }


    final public void dispose() {
        listeners.clear();
        disposeRenderer();
    }


    protected void disposeRenderer() {

    }
}
