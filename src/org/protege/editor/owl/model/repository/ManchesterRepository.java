package org.protege.editor.owl.model.repository;

import org.protege.editor.core.OntologyRepository;
import org.protege.editor.core.OntologyRepositoryEntry;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.util.OntologyURIShortFormProvider;
import org.semanticweb.owl.model.OWLOntologyURIMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
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
public class ManchesterRepository implements OntologyRepository {

    private String repositoryName;

    private URI repositoryLocation;

    private List<RepositoryEntry> entries;

    private OWLOntologyURIMapper uriMapper;


    public ManchesterRepository(String repositoryName, URI repositoryLocation) {
        this.repositoryName = repositoryName;
        this.repositoryLocation = repositoryLocation;
        entries = new ArrayList<RepositoryEntry>();
        uriMapper = new RepositoryURIMapper();
    }


    public void initialise() throws Exception {
    }


    public String getName() {
        return repositoryName;
    }


    public String getLocation() {
        return repositoryLocation.toString();
    }


    public void refresh() {
        fillRepository();
    }


    public Collection<OntologyRepositoryEntry> getEntries() {
        List<OntologyRepositoryEntry> ret = new ArrayList<OntologyRepositoryEntry>();
        ret.addAll(entries);
        return ret;
    }


    public List<Object> getMetaDataKeys() {
        return Collections.emptyList();
    }


    public void dispose() throws Exception {
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Implementation details


    private void fillRepository() {
        try {
            entries.clear();
            URI listURI = URI.create(repositoryLocation + "/list");
            BufferedReader br = new BufferedReader(new InputStreamReader(listURI.toURL().openStream()));
            String line;
            while((line = br.readLine()) != null) {
                try {
                    entries.add(new RepositoryEntry(new URI(line)));
                }
                catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class RepositoryEntry implements OntologyRepositoryEntry {

        private String shortName;

        private URI ontologyURI;

        private URI physicalURI;

        public RepositoryEntry(URI ontologyURI) {
            this.ontologyURI = ontologyURI;
            OntologyURIShortFormProvider sfp = new OntologyURIShortFormProvider();
            shortName = sfp.getShortForm(ontologyURI);
            physicalURI = URI.create(repositoryLocation + "/download?ontology=" + ontologyURI);
        }


        public String getOntologyShortName() {
            return shortName;
        }


        public URI getOntologyURI() {
            return ontologyURI;
        }


        public URI getPhysicalURI() {
            return physicalURI;
        }


        public String getEditorKitId() {
            return "org.protege.editor.owl.OWLEditorKitFactory";
        }


        public String getMetaData(Object key) {
            return null;
        }


        public void configureEditorKit(EditorKit editorKit) {
            ((OWLEditorKit) editorKit).getOWLModelManager().getOWLOntologyManager().addURIMapper(uriMapper);
        }


        public void restoreEditorKit(EditorKit editorKit) {
            ((OWLEditorKit) editorKit).getOWLModelManager().getOWLOntologyManager().removeURIMapper(uriMapper);

        }
    }


    private class RepositoryURIMapper implements OWLOntologyURIMapper {

        public URI getPhysicalURI(URI uri) {
            for(RepositoryEntry entry : entries) {
                if(entry.getOntologyURI().equals(uri)) {
                    return entry.getPhysicalURI();
                }
            }
            return null;
        }
    }
}
