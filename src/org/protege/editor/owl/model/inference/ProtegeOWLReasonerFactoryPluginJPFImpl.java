package org.protege.editor.owl.model.inference;


import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.ExtensionInstantiator;
import org.protege.editor.core.plugin.JPFUtil;
import org.protege.editor.core.plugin.PluginProperties;
import org.protege.editor.owl.model.OWLModelManager;
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
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ProtegeOWLReasonerFactoryPluginJPFImpl implements ProtegeOWLReasonerFactoryPlugin {
    private Logger logger = Logger.getLogger(ProtegeOWLReasonerFactoryPluginJPFImpl.class);
    
    public static final String NAME_PARAM = "name";


    private OWLModelManager owlModelManager;

    private IExtension extension;


    public ProtegeOWLReasonerFactoryPluginJPFImpl(OWLModelManager owlModelManager, IExtension extension) {
        this.owlModelManager = owlModelManager;
        this.extension = extension;
    }


    /**
     * Gets a <code>String</code> that represents the reasoner ID.
     */
    public String getId() {
        return extension.getUniqueIdentifier();
    }


    /**
     * Gets the name of the reasoner.  This should be
     * human readable, because it is generally used for
     * menu labels etc.
     */
    public String getName() {
        return PluginProperties.getParameterValue(extension, NAME_PARAM, "Reasoner " + System.currentTimeMillis());
    }


    public String getDocumentation() {
        return JPFUtil.getDocumentation(extension);
    }


    /**
     * Creates an instance of the plugin.  It is expected that
     * this instance will be "setup", but the instance's
     * initialise method will not have been called in the instantiation
     * process.
     */
    public ProtegeOWLReasonerFactory newInstance() throws ClassNotFoundException, IllegalAccessException,
                                                          InstantiationException {
        ExtensionInstantiator<ProtegeOWLReasonerFactory> instantiator = new ExtensionInstantiator<ProtegeOWLReasonerFactory>(
                extension);
        ProtegeOWLReasonerFactory reasoner = instantiator.instantiate();
        reasoner.setup(owlModelManager.getOWLOntologyManager(), getId(), getName());
        try {
            Method m = reasoner.getClass().getMethod("setOWLModelManager", OWLModelManager.class);
            m.invoke(reasoner, owlModelManager);
        }
        catch (Throwable t) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not set model manager for reasoner " + reasoner, t);
            }
        }
        return reasoner;
    }
}
