package org.protege.editor.owl.ui.renderer;


import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.ExtensionInstantiator;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.owl.model.OWLModelManager;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityColorProviderPluginJPFImpl implements OWLEntityColorProviderPlugin {

    private OWLModelManager owlModelManager;

    private IExtension extension;


    public OWLEntityColorProviderPluginJPFImpl(OWLModelManager owlModelManager, IExtension extension) {
        this.owlModelManager = owlModelManager;
        this.extension = extension;
    }


    public String getId() {
        return extension.getUniqueIdentifier();
    }


    public String getDocumentation() {
        return PluginUtilities.getInstance().getDocumentation(extension);
    }


    public OWLEntityColorProvider newInstance() throws ClassNotFoundException, IllegalAccessException,
                                                       InstantiationException {
        ExtensionInstantiator<OWLEntityColorProvider> instantiator = new ExtensionInstantiator<>(
                extension);
        OWLEntityColorProvider prov = instantiator.instantiate();
        prov.setup(owlModelManager);
        return prov;
    }
}
