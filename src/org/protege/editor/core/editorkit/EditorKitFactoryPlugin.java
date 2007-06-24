package org.protege.editor.core.editorkit;

import org.apache.log4j.Logger;
import org.eclipse.core.internal.registry.osgi.OSGIUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;


import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.protege.editor.core.plugin.ExtensionInstantiator;
import org.protege.editor.core.plugin.PluginUtilities;
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
 * Date: Mar 18, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * <p/>
 * An <code>EditorKitFactoryPlugin</code> encapsulates
 * details of an <code>EditorKitFactory</code> plugin.
 */
public class EditorKitFactoryPlugin {

    private static final Logger logger = Logger.getLogger(EditorKitFactoryPlugin.class);


    private IExtension extension;

    public static final String LABEL_PARAM = "label";


    public EditorKitFactoryPlugin(IExtension extension) {
        this.extension = extension;
    }


    public String getId() {
        return extension.getUniqueIdentifier();
    }


    /**
     * Gets the <code>EditorKitFactory</code> label.  This is
     * typically used for UI menu items etc.
     */
    public String getLabel() {
        String param = PluginUtilities.getAttribute(extension, LABEL_PARAM);
        if (param == null) {
            return "<Error: Label not defined!> " + extension;
        }
        return param;
    }


    public EditorKitFactory newInstance() {
        try {
            Bundle b = PluginUtilities.getBundle(extension);
            b.start();
            ExtensionInstantiator<EditorKitFactory> instantiator = new ExtensionInstantiator<EditorKitFactory>(extension);
            return instantiator.instantiate();
        }
        catch (Exception e) {
            logger.error("Exception caught", e);
        } 
        return null;
    }
}
