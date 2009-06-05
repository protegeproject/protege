package org.protege.editor.owl.ui.editor;

import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 15-Feb-2007<br><br>
 */
public abstract class AbstractOWLObjectEditor<E> implements OWLObjectEditor<E> {

    private OWLObjectEditorHandler<E> handler;


    public void setHandler(OWLObjectEditorHandler<E> handler) {
        this.handler = handler;
    }


    public OWLObjectEditorHandler<E> getHandler() {
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


    protected boolean checkSet(Object object, Class elementCls) {
        if (!(object instanceof Set)){
            return false;
        }

        for (Object item : (Set)object){
            if (!elementCls.isAssignableFrom(item.getClass())){
                return false;
            }
        }
        return true;
    }

    
    protected boolean checkList(Object object, Class elementCls) {
        if (!(object instanceof List)){
            return false;
        }

        for (Object item : (List)object){
            if (!elementCls.isAssignableFrom(item.getClass())){
                return false;
            }
        }
        return true;
    }


    public final void clear(){
        setEditedObject(null);
    }
}
