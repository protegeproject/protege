package org.protege.editor.owl.model.cache;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.renderer.OWLObjectRenderer;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 27, 2008<br><br>
 */
public class OWLObjectRenderingCache implements Disposable {

    Map<OWLObject, String> cache = new MyLRUMap<OWLObject, String>(50, 1, 50);

    private OWLModelManagerListener l = new OWLModelManagerListener(){
        public void handleChange(OWLModelManagerChangeEvent event) {
            clear();
        }
    };

    private OWLOntologyChangeListener l2 = new OWLOntologyChangeListener(){
        public void ontologiesChanged(List<? extends OWLOntologyChange> owlOntologyChanges) throws OWLException {
            clear();
        }
    };

    private OWLModelManager mngr;


    public OWLObjectRenderingCache(OWLModelManager owlModelManager) {
        this.mngr = owlModelManager;
        owlModelManager.addListener(l);
        owlModelManager.addOntologyChangeListener(l2);
    }


    public void clear() {
        cache.clear();
    }


    public String getRendering(OWLObject object, OWLObjectRenderer owlObjectRenderer) {
        String s = null;
        if (object instanceof OWLClassExpression){
            String userRendering = OWLExpressionUserCache.getInstance(mngr).getRendering((OWLClassExpression) object);
            if (userRendering != null){
                s = userRendering;
                cache.put(object, s);
            }
        }
        if (s == null){
            s = cache.get(object);
            if (s == null){
                s = owlObjectRenderer.render(object);
                cache.put(object, s);
            }
        }
        return s;
    }


    public void dispose() {
        clear();
        mngr.removeListener(l);
        mngr.removeOntologyChangeListener(l2);
    }


    class MyLRUMap<K,V> extends LinkedHashMap<K,V> {
        private int maxCapacity;

        public MyLRUMap(int initialCapacity, float loadFactor, int maxCapacity) {
            super(initialCapacity, loadFactor, true);
            this.maxCapacity = maxCapacity;
        }

        protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
            return size() >= this.maxCapacity;
        }
    }
}
