package org.protege.editor.core.plugin;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 28, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>

 * A <code>ProtegePlugin</code> acts as a factory for
 * <code>ProtegePluginInstance</code>.
 */
public interface ProtegePlugin<E extends ProtegePluginInstance> {

    /**
     * Gets the plugin Id.
     * @return A <code>String</code> that represents the Id of
     *         the plugin
     */
    public String getId();


    public String getDocumentation();


    /**
     * Creates an instance of the plugin.  It is expected that
     * this instance will be "setup", but the instance's
     * initialise method will not have been called in the instantiation
     * process.
     */
    public E newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException;
}
