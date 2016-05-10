package org.protege.editor.owl.ui.renderer;


import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.owl.ProtegeOWL;
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

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityColorProviderPluginLoader extends AbstractPluginLoader<OWLEntityColorProviderPlugin> {

    private OWLModelManager owlModelManager;


    public OWLEntityColorProviderPluginLoader(OWLModelManager owlModelManager) {
        super(ProtegeOWL.ID, OWLEntityColorProviderPlugin.ID);
        this.owlModelManager = owlModelManager;
    }

    protected OWLEntityColorProviderPlugin createInstance(IExtension extension) {
        return new OWLEntityColorProviderPluginJPFImpl(owlModelManager, extension);
    }
}
