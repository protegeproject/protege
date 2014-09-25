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
 * <p/>
 * A default implementation of the <code>PluginExtensionMatcher</code>
 * that just matches all plugin <code>Extensions</code>
 */
public class DefaultPluginExtensionMatcher implements PluginExtensionMatcher {


    /**
     * @return Always returns <code>true</code>
     */
    public boolean matches(IExtension extension) {
        return true;
    }
}
