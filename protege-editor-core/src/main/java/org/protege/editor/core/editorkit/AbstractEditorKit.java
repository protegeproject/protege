package org.protege.editor.core.editorkit;

import org.protege.editor.core.Disposable;
import org.protege.editor.core.editorkit.plugin.EditorKitHook;
import org.protege.editor.core.editorkit.plugin.EditorKitHookPlugin;
import org.protege.editor.core.editorkit.plugin.EditorKitHookPluginLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 15, 2008<br><br>
 */
public abstract class AbstractEditorKit<T extends EditorKitFactory> implements EditorKit {

    private final Logger logger = LoggerFactory.getLogger(AbstractEditorKit.class);

    private Map<Object, Disposable> objects = new HashMap<>();

    private T editorKitFactory;


    public AbstractEditorKit(T editorKitFactory) {
        this.editorKitFactory = editorKitFactory;
        initialise();
        initialiseCompleted();
    }


    protected abstract void initialise();

    protected void initialiseCompleted(){
        for (EditorKitHookPlugin editorKitHookPlugin : new EditorKitHookPluginLoader(this).getPlugins()){
            try {
                EditorKitHook instance = editorKitHookPlugin.newInstance();
                instance.initialise();
                put(editorKitHookPlugin.getId(), instance);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public void put(Object key, Disposable value) {
    	objects.put(key, value);
    }
    
    public Disposable get(Object key) {
    	return objects.get(key);
    }

    
    public T getEditorKitFactory() {
        return editorKitFactory;
    }
    
    

    public void dispose(){
        for (Disposable object : objects.values()){
            try {
                object.dispose();
            }
            catch (Exception e) {
                logger.error("An error occurred whilst disposing of an Editor Kit object.  Object: ", object, e);
            }
        }
        objects.clear();
    }
}
