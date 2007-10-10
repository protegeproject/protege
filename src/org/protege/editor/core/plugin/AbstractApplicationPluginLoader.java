package org.protege.editor.core.plugin;

import org.protege.editor.core.ProtegeApplication;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 30, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A helper class that loads application plugins - i.e. plugins
 * that extend extension points which are on the protege application
 * plugin.
 */
public abstract class AbstractApplicationPluginLoader<E> extends AbstractPluginLoader<E> {

    public AbstractApplicationPluginLoader(String extensionPointId) {
        super(ProtegeApplication.ID, extensionPointId);
    }
}
