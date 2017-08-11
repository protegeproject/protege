package org.protege.editor.owl.ui.library;

import com.google.common.base.Optional;
import org.protege.editor.owl.model.repository.MasterOntologyIDExtractor;
import org.protege.editor.owl.ui.ontology.imports.wizard.GetImportsVisitor;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportInfo;
import org.protege.xmlcatalog.EntryVisitor;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.*;
import org.protege.xmlcatalog.owlapi.XMLCatalogIRIMapper;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vblagodarov on 01-08-17.
 */
public class XMLCatalogManager {

    XMLCatalogIRIMapper iriMapper;
    XMLCatalogEntriesVisitor entriesVisitor;
    MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor();
    XMLCatalog catalog;

    public XMLCatalogManager(XMLCatalog xmlCatalog) {
        catalog = xmlCatalog;
        iriMapper = new XMLCatalogIRIMapper(xmlCatalog);
        entriesVisitor = new XMLCatalogEntriesVisitor();
        for (Entry entry : xmlCatalog.getEntries()) {
            entry.accept(entriesVisitor);
        }
    }

    public XMLCatalog getCatalog() {
        return catalog;
    }

    public Collection<ImportInfo> getImports() {
        Set<ImportInfo> imports = new HashSet<>();
        for (XMLCatalogUriEntry entry : getAllUriEntries()) {
            ImportInfo myImport = new ImportInfo();
            myImport.setImportLocation(IRI.create(entry.getEntry().getName()));
            myImport.setPhysicalLocation(entry.getPhysicalLocation());

            Optional<OWLOntologyID> id = extractor.getOntologyId(entry.getPhysicalLocation());
            //If ID is not set, the import will be ignored
            if (id.isPresent()) {
                myImport.setOntologyID(id.get());
            }
            imports.add(myImport);
        }
        return imports;
    }

    public boolean containsUri(URI physicalLocation) {
        if (physicalLocation == null) {
            return false;
        }
        return getAllUriEntries().stream().filter(
                entry -> entry.getPhysicalLocation().equals(physicalLocation)).findAny().isPresent();
    }

    public Collection<XMLCatalogUriEntry> getAllUriEntries() {
        Set<XMLCatalogUriEntry> entries = new HashSet<>();
        for (UriEntry uriEntry : entriesVisitor.getAllUriEntries()) {
            IRI ontologyIRI = IRI.create(uriEntry.getName());
            URI redirect = iriMapper.getDocumentIRI(ontologyIRI).toURI();
            entries.add(redirect == null ? new XMLCatalogUriEntry(ontologyIRI, uriEntry) :
                    new XMLCatalogUriEntry(ontologyIRI, uriEntry, redirect));
        }

        return entries;
    }

    public class XMLCatalogUriEntry {
        private UriEntry entry;
        private URI location;
        private IRI ontologyIRI;

        public XMLCatalogUriEntry(@Nonnull IRI ontologyIRI, @Nonnull UriEntry uriEntry) {
            this(ontologyIRI, uriEntry, uriEntry.getAbsoluteURI());
        }

        public XMLCatalogUriEntry(@Nonnull IRI ontologyIRI, @Nonnull UriEntry uriEntry, @Nonnull URI physicalLocation) {
            this.ontologyIRI = ontologyIRI;
            entry = uriEntry;
            location = physicalLocation;
        }

        public UriEntry getEntry() {
            return entry;
        }

        public IRI getOntologyIRI() {
            return ontologyIRI;
        }

        public URI getPhysicalLocation() {
            return location;
        }
    }

    private class XMLCatalogEntriesVisitor implements EntryVisitor {

        private Set<UriEntry> uriEntries = new HashSet<>();

        public Set<UriEntry> getAllUriEntries() {
            return uriEntries;
        }

        @Override
        public void visit(UriEntry entry) {
            uriEntries.add(entry);
        }

        @Override
        public void visit(GroupEntry entry) {
            for (Entry subEntry : entry.getEntries()) {
                subEntry.accept(this);
            }
        }

        @Override
        public void visit(PublicEntry entry) {

        }

        @Override
        public void visit(SystemEntry entry) {

        }

        @Override
        public void visit(RewriteSystemEntry entry) {

        }

        @Override
        public void visit(DelegatePublicEntry entry) {

        }

        @Override
        public void visit(DelegateSystemEntry entry) {

        }

        @Override
        public void visit(RewriteUriEntry entry) {

        }

        @Override
        public void visit(DelegateUriEntry entry) {

        }

        @Override
        public void visit(NextCatalogEntry entry) {

            try {
                XMLCatalog catalog = entry.getParsedCatalog();
                for (Entry subEntry : catalog.getEntries()) {
                    subEntry.accept(this);
                }
            } catch (IOException e) {
                LoggerFactory.getLogger(GetImportsVisitor.class)
                        .error("Ad error occurred whilst attempting to process the XMLCatalog file: {}", e);
            }
        }
    }
}
