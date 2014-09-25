package org.protege.editor.core;

import org.protege.editor.core.plugin.ProtegePluginInstance;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 18-Oct-2008<br><br>
 */
public abstract class OntologyRepositoryFactory implements ProtegePluginInstance {

    /**
     * Creates the repository.
     * @return An ontology repository.
     */
    public abstract OntologyRepository createRepository();
}
