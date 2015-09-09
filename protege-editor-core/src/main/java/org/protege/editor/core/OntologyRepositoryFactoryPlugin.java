package org.protege.editor.core;

import org.protege.editor.core.plugin.ProtegePlugin;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 18-Oct-2008<br><br>
 */
public interface OntologyRepositoryFactoryPlugin extends ProtegePlugin<OntologyRepositoryFactory> {

    public static final String EXTENSION_POINT_ID = "OntologyRepositoryFactory";
    
}
