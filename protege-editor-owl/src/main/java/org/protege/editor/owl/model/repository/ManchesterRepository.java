package org.protege.editor.owl.model.repository;

import org.protege.editor.core.OntologyRepository;
import org.protege.editor.core.OntologyRepositoryEntry;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 18-Oct-2008<br><br>
 */
public class ManchesterRepository implements OntologyRepository {

    private String repositoryName;

    private URI repositoryLocation;

    private List<RepositoryEntry> entries;

    private OWLOntologyIRIMapper iriMapper;


    public ManchesterRepository(String repositoryName, URI repositoryLocation) {
        this.repositoryName = repositoryName;
        this.repositoryLocation = repositoryLocation;
        entries = new ArrayList<>();
        iriMapper = new RepositoryIRIMapper();
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
        List<OntologyRepositoryEntry> ret = new ArrayList<>();
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

        public RepositoryEntry(URI ontologyIRI) {
            this.ontologyURI = ontologyIRI;
            OntologyIRIShortFormProvider sfp = new OntologyIRIShortFormProvider();
            shortName = sfp.getShortForm(IRI.create(ontologyIRI));
            physicalURI = URI.create(repositoryLocation + "/download?ontology=" + ontologyIRI);
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
            ((OWLEditorKit) editorKit).getOWLModelManager().getOWLOntologyManager().addIRIMapper(iriMapper);
        }


        public void restoreEditorKit(EditorKit editorKit) {
            ((OWLEditorKit) editorKit).getOWLModelManager().getOWLOntologyManager().removeIRIMapper(iriMapper);

        }
    }


    private class RepositoryIRIMapper implements OWLOntologyIRIMapper {

        public IRI getDocumentIRI(IRI iri) {
            for(RepositoryEntry entry : entries) {
                if(entry.getOntologyURI().equals(iri.toURI())) {
                    return IRI.create(entry.getPhysicalURI());
                }
            }
            return null;
        }
    }
}
