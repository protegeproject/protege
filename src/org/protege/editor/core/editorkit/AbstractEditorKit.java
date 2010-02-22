package org.protege.editor.core.editorkit;

import org.apache.log4j.Logger;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.editorkit.plugin.EditorKitHook;
import org.protege.editor.core.editorkit.plugin.EditorKitHookPlugin;
import org.protege.editor.core.editorkit.plugin.EditorKitHookPluginLoader;

import java.util.HashMap;
import java.util.Map;
/*
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
 * Date: Oct 15, 2008<br><br>
 */
public abstract class AbstractEditorKit<T extends EditorKitFactory> implements EditorKit {

    private static final Logger logger = Logger.getLogger(AbstractEditorKit.class);

    private Map<Object, Disposable> objects = new HashMap<Object, Disposable>();

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


    public T getEditorKitFactory() {
        return editorKitFactory;
    }


    public <S extends Disposable> void put(Object key, S object) {
        objects.put(key, object);
    }


    @SuppressWarnings("unchecked")
    public <S extends Disposable> S get(Object key) {
        return (S) objects.get(key);
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
