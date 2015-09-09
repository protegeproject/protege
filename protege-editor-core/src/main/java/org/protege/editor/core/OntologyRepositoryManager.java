package org.protege.editor.core;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 18-Oct-2008<br><br>
 */
public class OntologyRepositoryManager {

    private static OntologyRepositoryManager instance;

    private List<OntologyRepository> repositories;

    private OntologyRepositoryManager() {
        repositories = new ArrayList<OntologyRepository>();
    }

    public static synchronized OntologyRepositoryManager getManager() {
        if(instance == null) {
            instance = new OntologyRepositoryManager();
        }
        return instance;
    }


    public void addRepository(OntologyRepository repository) {
        repositories.add(repository);
    }

    public Collection<OntologyRepository> getOntologyRepositories() {
        return Collections.unmodifiableList(repositories);
    }

}
