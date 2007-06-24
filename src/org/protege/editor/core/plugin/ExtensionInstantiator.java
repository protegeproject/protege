package org.protege.editor.core.plugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExtension;



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
 * Date: Mar 17, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A utility class that creates instances of a plugin
 * based on its <code>Extension</code>.  The plugin extension
 * is expected to have a parameter that has an identifier
 * whose name matches the <code>CLASS_PARAM_NAME</code> field
 * value.
 */
public class ExtensionInstantiator<E> {

    private IExtension extension;


    /**
     * Creates a plugin instantiator that will create instances
     * of a plugin identified by the specified extension.
     * @param extension The <code>Extension</code> that describes the
     *                  plugin to be instantiated.  Note that this extension is
     *                  expected to have a parameter with a name matching the
     *                  <code>ExtensionInstantiator.CLASS_PARAM_NAME</code>.
     */
    public ExtensionInstantiator(IExtension extension) {
        this.extension = extension;
    }


    /**
     * Creates an instance of the plugin
     * @return An instance of type <code>E</code>.  Note that a <code>null</code>
     *         value will be returned if there was a problem creating the instance.
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    @SuppressWarnings("unchecked")
    public E instantiate() throws ClassCastException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Object o = PluginUtilities.getInstance().getExtensionObject(extension, PluginProperties.CLASS_PARAM_NAME);
        return (E) o;
    }
}
