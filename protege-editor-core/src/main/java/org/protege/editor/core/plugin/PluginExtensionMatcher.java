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

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>

 * This interface is used by the plugin utilities to filter
 * out certain types of plugin extensions.  Users of
 * <code>PluginExtensionMatcher</code> implementations should
 * define how the matcher is used.
 */
public interface PluginExtensionMatcher {

    /**
     * Determines whether the specified <code>Extension</code>
     * constitutes a "match" or not.
     * @param extension The <code>Extension</code> to test.
     * @return <code>true</code> if the <code>Extension</code> matches
     *         or <code>false</code> if the <code>Extension</code> doesn't match.
     */
    public boolean matches(IExtension extension);
}
