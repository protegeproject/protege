package org.protege.editor.core.plugin;

import org.eclipse.core.runtime.IExtension;




/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 30, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class JPFUtil {
    public final static String EXTENSION_DOCUMENTATION = "documentation";

    public static String getDocumentation(IExtension extension) {
        return PluginUtilities.getAttribute(extension, EXTENSION_DOCUMENTATION);
    }
}
