package org.protege.editor.owl.model.inference;


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
 * Date: 19-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ProtegeOWLReasonerPluginLoader extends AbstractPluginLoader<ProtegeOWLReasonerPlugin> {

    private OWLModelManager owlModelManager;


    public ProtegeOWLReasonerPluginLoader(OWLModelManager owlModelManager) {
        super(ProtegeOWL.ID, ProtegeOWLReasonerPlugin.REASONER_PLUGIN_TYPE_ID);
        this.owlModelManager = owlModelManager;
    }


    /**
     * This method needs to be overriden to create an instance
     * of the desired plugin, based on the plugin <code>Extension</code>
     * @param extension The <code>Extension</code> that describes the
     *                  Java Plugin Framework extension.
     * @return A plugin object (typically some sort of wrapper around
     *         the extension)
     */
    protected ProtegeOWLReasonerPlugin createInstance(IExtension extension) {
        return new ProtegeOWLReasonerPluginJPFImpl(owlModelManager, extension);
    }
}
