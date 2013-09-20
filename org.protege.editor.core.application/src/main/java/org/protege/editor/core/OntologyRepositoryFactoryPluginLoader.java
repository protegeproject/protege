package org.protege.editor.core;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractApplicationPluginLoader;


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 18-Oct-2008<br><br>
 */
public class OntologyRepositoryFactoryPluginLoader extends AbstractApplicationPluginLoader<OntologyRepositoryFactoryPlugin> {

    public OntologyRepositoryFactoryPluginLoader() {
        super(OntologyRepositoryFactoryPlugin.EXTENSION_POINT_ID);
    }


    protected OntologyRepositoryFactoryPlugin createInstance(IExtension extension) {
        return new OntologyRepositoryFactoryPluginImpl(extension);
    }
}
