package org.protege.editor.core.editorkit;

import org.protege.editor.core.Disposable;
import org.protege.editor.core.editorkit.plugin.EditorKitHook;
import org.protege.editor.core.editorkit.plugin.EditorKitHookPlugin;
import org.protege.editor.core.editorkit.plugin.EditorKitHookPluginLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 15, 2008<br><br>
 */
public abstract class AbstractEditorKit<T extends EditorKitFactory> implements EditorKit {

    private final Logger logger = LoggerFactory.getLogger(AbstractEditorKit.class);

    private final Map<Object, Disposable> objects = new HashMap<>();

    private final T editorKitFactory;


    public AbstractEditorKit(@Nonnull T editorKitFactory) {
        this.editorKitFactory = checkNotNull(editorKitFactory);
    }

    public void put(@Nullable Object key, @Nullable Disposable value) {
    	objects.put(key, value);
    }

    @Nullable
    public Disposable get(@Nullable Object key) {
    	return objects.get(key);
    }

    @Nonnull
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
