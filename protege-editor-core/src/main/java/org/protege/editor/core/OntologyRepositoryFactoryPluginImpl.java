package org.protege.editor.core;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.ExtensionInstantiator;
import org.protege.editor.core.plugin.JPFUtil;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 18-Oct-2008<br><br>
 */
public class OntologyRepositoryFactoryPluginImpl implements OntologyRepositoryFactoryPlugin {

    private IExtension extension;


    public OntologyRepositoryFactoryPluginImpl(IExtension extension) {
        this.extension = extension;
    }


    public String getId() {
        return extension.getUniqueIdentifier();
    }


    public String getDocumentation() {
        return JPFUtil.getDocumentation(extension);
    }


    public OntologyRepositoryFactory newInstance() throws ClassNotFoundException, IllegalAccessException,
                                                         InstantiationException {
        ExtensionInstantiator<OntologyRepositoryFactory> instantiator = new ExtensionInstantiator<>(extension);
        return instantiator.instantiate();
    }
}
