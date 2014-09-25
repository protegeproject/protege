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
