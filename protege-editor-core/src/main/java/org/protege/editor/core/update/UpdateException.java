package org.protege.editor.core.update;

import com.google.common.base.Optional;

import java.net.URL;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 4, 2008<br><br>
 */
public class UpdateException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = -6812551693275528899L;

    private final Optional<String> pluginId;

    public UpdateException(String id, URL url, String message) {
        super(id + ": problem with update file (" + url + "). " + message);
        this.pluginId = Optional.fromNullable(id);
    }

    /**
     * Gets the plugin Id.
     * @return The plugin id. Not {@code null}, but may not be present.
     */
    public Optional<String> getPluginId() {
        return pluginId;
    }
}
