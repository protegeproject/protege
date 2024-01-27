package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.protege.xmlcatalog.EntryVisitor;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.DelegatePublicEntry;
import org.protege.xmlcatalog.entry.DelegateSystemEntry;
import org.protege.xmlcatalog.entry.DelegateUriEntry;
import org.protege.xmlcatalog.entry.Entry;
import org.protege.xmlcatalog.entry.GroupEntry;
import org.protege.xmlcatalog.entry.NextCatalogEntry;
import org.protege.xmlcatalog.entry.PublicEntry;
import org.protege.xmlcatalog.entry.RewriteSystemEntry;
import org.protege.xmlcatalog.entry.RewriteUriEntry;
import org.protege.xmlcatalog.entry.SystemEntry;
import org.protege.xmlcatalog.entry.UriEntry;
import org.semanticweb.owlapi.model.IRI;
import org.slf4j.LoggerFactory;

public class GetImportsVisitor implements EntryVisitor {
   
    private Collection<ImportInfo> imports = new ArrayList<>();
    
    public Collection<ImportInfo> getImports() {
        return imports;
    }

    public void visit(UriEntry entry) {
        ImportInfo myImport = new ImportInfo();
        myImport.setImportLocation(IRI.create(entry.getName()));
        myImport.setPhysicalLocation(entry.getAbsoluteURI());
        imports.add(myImport);
    }

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

    public void visit(GroupEntry entry) {
    	for (Entry subEntry : entry.getEntries()) {
    		subEntry.accept(this);
    	}
    }

    public void visit(PublicEntry entry) {
    }

    public void visit(SystemEntry entry) {
    }

    public void visit(RewriteSystemEntry entry) {
    }

    public void visit(DelegatePublicEntry entry) {
    }

    public void visit(DelegateSystemEntry entry) {
    }

    public void visit(RewriteUriEntry entry) {
    }

    public void visit(DelegateUriEntry entry) {
    }
}
