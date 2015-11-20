package org.protege.editor.owl.model.io;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.ExtensionInstantiator;
import org.protege.editor.core.plugin.JPFUtil;
import org.protege.editor.owl.OWLEditorKit;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 16-Sep-2008<br><br>
 */
public class IOListenerPluginImpl implements IOListenerPlugin {

    private IExtension iExtension;

    private OWLEditorKit editorKit;

    public IOListenerPluginImpl(IExtension iExtension, OWLEditorKit editorKit) {
        this.iExtension = iExtension;
        this.editorKit = editorKit;
    }


    public String getId() {
        return ID;
    }


    public String getDocumentation() {
        return JPFUtil.getDocumentation(iExtension);
    }


    public IOListenerPluginInstance newInstance() throws ClassNotFoundException, IllegalAccessException,
                                                         InstantiationException {
        ExtensionInstantiator<IOListenerPluginInstance> instantiator = new ExtensionInstantiator<IOListenerPluginInstance>(iExtension);
        IOListenerPluginInstance instance = instantiator.instantiate();
        instance.setup(editorKit);
        return instance;
    }
}
