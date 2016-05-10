package org.protege.editor.owl.model.inference;


import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractProtegePlugin;
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

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ProtegeOWLReasonerPluginJPFImpl extends AbstractProtegePlugin<ProtegeOWLReasonerInfo> implements ProtegeOWLReasonerPlugin {

    public static final String NAME_PARAM = "name";


    private OWLModelManager owlModelManager;

    private IExtension extension;


    public ProtegeOWLReasonerPluginJPFImpl(OWLModelManager owlModelManager, IExtension extension) {
    	super(extension);
        this.owlModelManager = owlModelManager;
        this.extension = extension;
    }


    /**
     * Gets the name of the reasoner.  This should be
     * human readable, because it is generally used for
     * menu labels etc.
     */
    public String getName() {
    	return getPluginProperty(NAME_PARAM, "Reasoner " + System.currentTimeMillis());
    }

    /**
     * Creates an instance of the plugin.  It is expected that
     * this instance will be "setup", but the instance's
     * initialise method will not have been called in the instantiation
     * process.
     */
    public ProtegeOWLReasonerInfo newInstance() throws ClassNotFoundException, IllegalAccessException,
                                                          InstantiationException {
        ProtegeOWLReasonerInfo reasoner = super.newInstance();
        reasoner.setup(owlModelManager.getOWLOntologyManager(), getId(), getName());
        reasoner.setOWLModelManager(owlModelManager);
        return reasoner;
    }
}
