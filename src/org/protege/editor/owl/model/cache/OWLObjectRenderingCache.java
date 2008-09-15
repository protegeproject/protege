package org.protege.editor.owl.model.cache;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.renderer.OWLObjectRenderer;
import org.semanticweb.owl.model.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 27, 2008<br><br>
 */
public class OWLObjectRenderingCache {

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
        if (object instanceof OWLDescription){
            String userRendering = OWLExpressionUserCache.getInstance(mngr).getRendering((OWLDescription) object);
            if (userRendering != null){
                s = userRendering;
                cache.put(object, s);
            }
        }
        if (s == null){
            s = cache.get(object);
            if (s == null){
                s = owlObjectRenderer.render(object, null);
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
