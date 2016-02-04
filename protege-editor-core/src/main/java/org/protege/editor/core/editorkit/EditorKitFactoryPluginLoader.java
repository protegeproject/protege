package org.protege.editor.core.editorkit;


import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.plugin.DefaultPluginExtensionMatcher;
import org.protege.editor.core.plugin.PluginExtensionFilter;

import java.util.HashSet;
import java.util.Set;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 18, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class EditorKitFactoryPluginLoader {


    public Set<EditorKitFactoryPlugin> getPlugins() {
        Set<EditorKitFactoryPlugin> result = new HashSet<>();
        PluginExtensionFilter filter = new PluginExtensionFilter(ProtegeApplication.ID,
                                                                 EditorKitFactory.EXTENSION_POINT_ID,
                                                                 new DefaultPluginExtensionMatcher());
        for (IExtension ext : filter.getExtensions()) {
            result.add(new EditorKitFactoryPlugin(ext));
        }
        return result;
    }
}
