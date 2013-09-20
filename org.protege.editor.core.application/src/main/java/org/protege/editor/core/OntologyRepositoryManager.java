package org.protege.editor.core;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
/*
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
