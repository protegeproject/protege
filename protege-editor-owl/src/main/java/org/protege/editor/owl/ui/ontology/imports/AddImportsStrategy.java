package org.protege.editor.owl.ui.ontology.imports;

import com.google.common.util.concurrent.*;
import org.protege.editor.core.log.LogBanner;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.io.*;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.editor.owl.model.selection.ontologies.ImportsClosureOntologySelectionStrategy;
import org.protege.editor.owl.ui.library.XMLCatalogManager;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportInfo;
import org.protege.editor.owl.ui.util.ProgressDialog;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.UriEntry;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.OntologyCopy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 12 May 16
 */
public class AddImportsStrategy {

    private final Logger logger = LoggerFactory.getLogger(AddImportsStrategy.class);

    private final OWLEditorKit editorKit;

    private final OWLOntology toOntology;

    private final Collection<ImportInfo> importInfo;

    private final ProgressDialog dlg = new ProgressDialog();

    private final ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());

    public AddImportsStrategy(OWLEditorKit editorKit, OWLOntology toOntology, Collection<ImportInfo> importInfo) {
        this.editorKit = editorKit;
        this.toOntology = toOntology;
        this.importInfo = new ArrayList<>(importInfo);
    }

    public void addImports() {
        addImportsInOtherThread();
    }

    private void addImportsInOtherThread() {
		service.submit(() -> {			
			try {
				List<OWLOntologyChange> result = loadImportsInternal();
				SwingUtilities.invokeLater(() -> {
					logger.info("Adding imports statements");
					editorKit.getModelManager().applyChanges(result);
                    editorKit.getOWLModelManager().setActiveOntologiesStrategy(new ImportsClosureOntologySelectionStrategy(editorKit.getOWLModelManager()));
					logger.info("Finished adding imports");
				});
				return result;
			} finally {
				dlg.setVisible(false);
			}
		});
		dlg.setVisible(true);
    }

    private List<OWLOntologyChange> loadImportsInternal() {
        OntologyCatalogManager catalogManager = editorKit.getOWLModelManager().getOntologyCatalogManager();
        XMLCatalogManager xmlCatalogManager = null;
        OWLOntology ontology = editorKit.getModelManager().getActiveOntology();
        IRI importersDocumentLocation = ontology.getOWLOntologyManager().getOntologyDocumentIRI(ontology);
        File f = null;
        XMLCatalog catalog = null;
        boolean isLocalFile=false;
        if (UIUtil.isLocalFile(importersDocumentLocation.toURI())) {
            f = new File(importersDocumentLocation.toURI());
            catalog = editorKit.getModelManager().addRootFolder(f.getParentFile());
            xmlCatalogManager = new XMLCatalogManager(catalogManager.getActiveCatalog());
            isLocalFile = true;
        }

        List<OWLOntologyChange> changes = new ArrayList<>();
        for (ImportInfo importParameters : importInfo) {
            logger.info(LogBanner.start("Importing ontology and imports closure"));
            logger.info("Processing {}", importParameters.getImportsDeclarationIRI());
            dlg.setMessage("Importing " + importParameters.getImportsDeclarationIRI());
            IRI importedOntologyDocumentIRI = importParameters.getImportsDeclarationIRI();

            if(isLocalFile){
                URI physicalLocationURI = CatalogUtilities.relativize(importParameters.getPhysicalLocation(), catalog);
                if(!xmlCatalogManager.containsUri(physicalLocationURI)){
                    catalog.addEntry(0, new UriEntry("Imports Wizard Entry", catalog, importedOntologyDocumentIRI.toURI().toString(), physicalLocationURI, null));
                }
                try {
                    CatalogUtilities.save(catalog, OntologyCatalogManager.getCatalogFile(f.getParentFile()));
                } catch (IOException e) {
                    logger.warn("An error occurred whilst saving the catalog file: {}", e);
                }
            }

            OWLOntologyManager man = editorKit.getOWLModelManager().getOWLOntologyManager();
            OWLImportsDeclaration decl = man.getOWLDataFactory().getOWLImportsDeclaration(importedOntologyDocumentIRI);
            if (!man.contains(importParameters.getOntologyID())) {
                try {
                    OWLOntologyManager loadingManager = OWLManager.createConcurrentOWLOntologyManager();
                    loadingManager.getIRIMappers().add(man.getIRIMappers());
                    ProgressDialogOntologyLoaderListener listener = new ProgressDialogOntologyLoaderListener(dlg, logger);
                    loadingManager.addOntologyLoaderListener(listener);
                    loadingManager.loadOntologyFromOntologyDocument(
                            new IRIDocumentSource(IRI.create(importParameters.getPhysicalLocation())),
                            new OWLOntologyLoaderConfiguration().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT));
                    loadingManager.removeOntologyLoaderListener(listener);
                    for(OWLOntology importedOntology : loadingManager.getOntologies()) {
                        IRI ontologyDocumentIRI = loadingManager.getOntologyDocumentIRI(importedOntology);
                        if (!man.contains(importedOntology.getOntologyID())) {
                            logger.info("Copying imported ontology: {}", ontologyDocumentIRI);
                            man.copyOntology(importedOntology, OntologyCopy.MOVE);
                        }
                        else {
                            logger.info("Not copying ontology {} as it is already loaded", ontologyDocumentIRI);
                        }
                    }
                    if (importParameters.getOntologyID() != null && !importParameters.getOntologyID().isAnonymous()) {
                        OWLOntology importedOnt = man.getOntology(importParameters.getOntologyID());
                        if (importedOnt == null) {
                            logger.warn("Imported ontology has unexpected id. " +
                                    "During imports processing we anticipated " + importParameters.getOntologyID());
                            continue;
                        }
                    }
                } catch (OWLOntologyCreationException e) {
                    logger.error("There was a problem loading the ontology from {}.  Error: {}", importedOntologyDocumentIRI, e.getMessage(), e);
                    JOptionPane.showMessageDialog(editorKit.getOWLWorkspace(), "An error occurred whilst the ontology at " + importedOntologyDocumentIRI + " was being loaded.", "Error loading ontology", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            changes.add(new AddImport(toOntology, decl));
            logger.info(LogBanner.end());
        }
        return changes;
    }
}
