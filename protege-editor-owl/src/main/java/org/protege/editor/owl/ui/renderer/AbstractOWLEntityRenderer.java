package org.protege.editor.owl.ui.renderer;

import java.util.ArrayList;
import java.util.List;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 30-Aug-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractOWLEntityRenderer implements OWLModelManagerEntityRenderer {

    private OWLModelManager mngr;

    private List<OWLEntityRendererListener> listeners = new ArrayList<>();

    private OWLOntologyChangeListener l = owlOntologyChanges -> processChanges(owlOntologyChanges);

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

    // just wrap the render method
    public final String getShortForm(OWLEntity owlEntity) {
        return render(owlEntity);
    }
    
    public String render(OWLEntity owlEntity) {
        return render(owlEntity.getIRI());
    }


    protected void processChanges(List<? extends OWLOntologyChange> changes) {
    }


    protected OWLModelManager getOWLModelManager(){
        return mngr;
    }


    protected void fireRenderingChanged(OWLEntity entity) {
        for (OWLEntityRendererListener listener : new ArrayList<>(listeners)) {
            listener.renderingChanged(entity, this);
        }
    }
    
    public void ontologiesChanged() {

    }


    final public void dispose() {
        listeners.clear();
        mngr.removeOntologyChangeListener(l);
        disposeRenderer();
    }


    protected abstract void disposeRenderer();
}
