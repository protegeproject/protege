package org.protege.editor.owl.ui.renderer;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeListener;

import java.util.ArrayList;
import java.util.List;


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

    private OWLModelManager mngr;

    private List<OWLEntityRendererListener> listeners = new ArrayList<OWLEntityRendererListener>();

    private OWLOntologyChangeListener l = new OWLOntologyChangeListener(){
        public void ontologiesChanged(List<? extends OWLOntologyChange> owlOntologyChanges) throws OWLException {
            processChanges(owlOntologyChanges);
        }
    };

    public void setup(OWLModelManager owlModelManager){
        this.mngr = owlModelManager;
        mngr.addOntologyChangeListener(l);
    }


    public void addListener(OWLEntityRendererListener listener) {
        listeners.add(listener);
    }


    public void removeListener(OWLEntityRendererListener listener) {
        listeners.remove(listener);
    }


    protected void processChanges(List<? extends OWLOntologyChange> changes) {
    }


    protected OWLModelManager getOWLModelManager(){
        return mngr;
    }


    protected void fireRenderingChanged(OWLEntity entity) {
        for (OWLEntityRendererListener listener : new ArrayList<OWLEntityRendererListener>(listeners)) {
            listener.renderingChanged(entity, this);
        }
    }


    final public void dispose() {
        listeners.clear();
        mngr.removeOntologyChangeListener(l);
        disposeRenderer();
    }


    protected abstract void disposeRenderer();
}
