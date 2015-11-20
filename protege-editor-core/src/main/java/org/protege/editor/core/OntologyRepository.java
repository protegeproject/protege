package org.protege.editor.core;

import java.util.Collection;
import java.util.List;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 18-Oct-2008<br><br>
 */
public interface OntologyRepository extends Disposable {

    /**
     * Gets the name of the repository
     * @return A short name for the repository
     */
    String getName();

    /**
     * Gets a description of the location of the repository
     * @return A human readable description of the repository location
     */
    String getLocation();


    /**
     * Ensures the repository is up to date
     */
    void refresh();

    Collection<OntologyRepositoryEntry> getEntries();
    
    List<Object> getMetaDataKeys();
}
