package org.protege.editor.owl.ui.ontology.imports;

import com.google.common.util.concurrent.*;
import org.protege.editor.core.log.LogBanner;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.io.*;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportInfo;
import org.protege.editor.owl.ui.util.ProgressDialog;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.UriEntry;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.OntologyCopy;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        List<OWLOntologyChange> changes = new ArrayList<>();
        for (ImportInfo importParameters : importInfo) {
            logger.info(LogBanner.start("Importing ontology and imports closure"));
            logger.info("Processing {}", importParameters.getImportsDeclarationIRI());
            dlg.setMessage("Importing " + importParameters.getImportsDeclarationIRI());
            IRI importedOntologyDocumentIRI = importParameters.getImportsDeclarationIRI();
            URI physicalLocation = importParameters.getPhysicalLocation();

            OntologyCatalogManager catalogManager = editorKit.getOWLModelManager().getOntologyCatalogManager();
            if (willRedirectTotheWrongPlace(catalogManager, importedOntologyDocumentIRI, physicalLocation)) {
                OWLOntology activeOntology = editorKit.getModelManager().getActiveOntology();
                addImportMapping(activeOntology, importedOntologyDocumentIRI, IRI.create(physicalLocation));
            }

            OWLOntologyManager man = editorKit.getOWLModelManager().getOWLOntologyManager();
            OWLImportsDeclaration decl = man.getOWLDataFactory().getOWLImportsDeclaration(importedOntologyDocumentIRI);
            if (!man.contains(importParameters.getOntologyID())) {
                try {
                    OWLOntologyManager loadingManager = OWLManager.createConcurrentOWLOntologyManager();
                    loadingManager.getIRIMappers()
                            .add(man.getIRIMappers());
                    ProgressDialogOntologyLoaderListener listener = new ProgressDialogOntologyLoaderListener(dlg, logger);
                    loadingManager.addOntologyLoaderListener(listener);
                    loadingManager.loadOntologyFromOntologyDocument(
                            new IRIDocumentSource(IRI.create(physicalLocation)),
                            new OWLOntologyLoaderConfiguration().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT));
                    loadingManager.removeOntologyLoaderListener(listener);
//                        editorKit.getModelManager().fireEvent(EventType.ONTOLOGY_LOADED);
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
                }
            }
            changes.add(new AddImport(toOntology, decl));
            logger.info(LogBanner.end());
        }
        return changes;
    }

    private static boolean willRedirectTotheWrongPlace(OntologyCatalogManager catalogManager, IRI importLocation, URI physicalLocation) {
        if (catalogManager.getRedirect(importLocation.toURI()) == null) {
            return !importLocation.equals(IRI.create(physicalLocation));
        }
        else {
            return !physicalLocation.equals(catalogManager.getRedirect(importLocation.toURI()));
        }
    }

    private void addImportMapping(OWLOntology ontology, IRI importLocation, IRI physicalLocation) {
        OWLOntologyManager manager = ontology.getOWLOntologyManager();

        manager.getIRIMappers().add(new SimpleIRIMapper(importLocation, physicalLocation));
        IRI importersDocumentLocation = manager.getOntologyDocumentIRI(ontology);
        if (UIUtil.isLocalFile(importersDocumentLocation.toURI())) {
            File f = new File(importersDocumentLocation.toURI());
            XMLCatalog catalog = editorKit.getModelManager().addRootFolder(f.getParentFile());
            URI physicalUri = CatalogUtilities.relativize(physicalLocation.toURI(), catalog);
            catalog.addEntry(0, new UriEntry("Imports Wizard Entry", catalog, importLocation.toURI().toString(), physicalUri, null));
            try {
                CatalogUtilities.save(catalog, OntologyCatalogManager.getCatalogFile(f.getParentFile()));
            } catch (IOException e) {
                logger.warn("An error occurred whilst saving the catalog file: {}", e);
            }
        }
    }


}
