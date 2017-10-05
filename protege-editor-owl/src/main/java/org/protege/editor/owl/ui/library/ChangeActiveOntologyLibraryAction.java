package org.protege.editor.owl.ui.library;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.core.ui.workspace.WorkspaceFrame;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.protege.editor.owl.ui.ontology.imports.AddImportsStrategy;
import org.protege.editor.owl.ui.ontology.imports.wizard.GetImportsVisitor;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportInfo;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by vblagodarov on 24-07-17.
 */
public class ChangeActiveOntologyLibraryAction extends ProtegeOWLAction {
    public void actionPerformed(ActionEvent e) {
        try {
            WorkspaceFrame frame = ProtegeManager.getInstance().getFrame(getOWLWorkspace());
            File catalogFile = UIUtil.openFile(
                    frame,
                    "Choose catalog file containing ontology repository information",
                    "Choose XML Catalog",
                    Collections.singleton("xml"));
            if (catalogFile != null) {
                OntologyCatalogManager catalogManager = getOWLModelManager().getOntologyCatalogManager();
                catalogManager.changeActiveCatalog(catalogFile);
                XMLCatalog activeCatalog = catalogManager.getActiveCatalog();
                GetImportsVisitor getter = new GetImportsVisitor();
                for (Entry entry : activeCatalog.getEntries()) {
                    entry.accept(getter);
                }
                OWLOntology activeOntology = getOWLModelManager().getActiveOntology();
                XMLCatalogManager xmlCatalogManager = new XMLCatalogManager(activeCatalog);
                Set<ImportInfo> importsToImport = xmlCatalogManager.getImports().stream().filter(
                        importInfo -> activeOntology.getDirectImportsDocuments().contains(importInfo.getImportLocation())).collect(Collectors.toSet());

                AddImportsStrategy ais = new AddImportsStrategy(getOWLEditorKit(), activeOntology, importsToImport);
                ais.addImports();
            }
        } catch (Exception ex) {
            LoggerFactory.getLogger(EditActiveOntologyLibraryAction.class)
                    .error("An error occurred whilst attempting to change ontology catalog: {}", e);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }
}
