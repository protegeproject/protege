package org.protege.editor.core.editorkit;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.protege.editor.core.Disposable;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 15, 2008<br><br>
 */
public abstract class AbstractEditorKit<T extends EditorKitFactory> implements EditorKit {

    private static final Logger logger = Logger.getLogger(AbstractEditorKit.class);

    private Map<Object, Disposable> objects = new HashMap<Object, Disposable>();

    private T editorKitFactory;


    public AbstractEditorKit(T editorKitFactory) {
        this.editorKitFactory = editorKitFactory;
        initialise();
    }


    protected abstract void initialise();
    
    public T getEditorKitFactory() {
        return editorKitFactory;
    }

    public void dispose(){
        for (Disposable object : objects.values()){
            try {
                object.dispose();
            }
            catch (Exception e) {
                logger.error(e);
            }
        }
        objects.clear();
    }
}
