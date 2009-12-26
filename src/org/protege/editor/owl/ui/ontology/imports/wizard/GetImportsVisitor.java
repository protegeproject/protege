package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.util.ArrayList;
import java.util.Collection;

import org.protege.editor.core.ProtegeApplication;
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

public class GetImportsVisitor implements EntryVisitor {
   
    private Collection<ImportInfo> imports = new ArrayList<ImportInfo>();
    
    public Collection<ImportInfo> getImports() {
        return imports;
    }

    @Override
    public void visit(UriEntry entry) {
        try {
            ImportInfo myImport = new ImportInfo();
            myImport.setImportLocation(IRI.create(entry.getName()));
            myImport.setPhysicalLocation(entry.getAbsoluteURI());
            imports.add(myImport);
        }
        catch (Throwable t) {
            ProtegeApplication.getErrorLog().logError(t);
        }
    }

    @Override
    public void visit(NextCatalogEntry entry) {
        try {
            XMLCatalog catalog = entry.getParsedCatalog();
            for (Entry subEntry : catalog.getEntries()) {
                subEntry.accept(this);
            }
        }
        catch (Throwable t) {
            ProtegeApplication.getErrorLog().logError(t);
        }
    }

    @Override
    public void visit(GroupEntry entry) {
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
}
