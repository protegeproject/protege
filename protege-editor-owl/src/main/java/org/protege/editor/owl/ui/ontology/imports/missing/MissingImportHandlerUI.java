package org.protege.editor.owl.ui.ontology.imports.missing;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.MissingImportHandler;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.entry.UriEntry;
import org.semanticweb.owlapi.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static javax.swing.JOptionPane.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 31-Aug-2006<br><br>
 *
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MissingImportHandlerUI implements MissingImportHandler {

    private static final String RESOLVE_MISSING_IMPORT_TITLE = "Resolve missing import?";

    /**
     * Missing import template, that has a param for the imported ontology IRI
     */
    private static final String MISSING_IMPORT_MSG = "<html><body>The system couldn't locate the ontology<br><br><b>%s</b><br><br>Would you like to attempt to resolve the missing import?</body></html>";

    private final static Logger logger = LoggerFactory.getLogger(MissingImportHandlerUI.class);

    private final OWLEditorKit owlEditorKit;

    private boolean noToAll = false;


    public MissingImportHandlerUI(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
    }

    public void reset() {
        noToAll = false;
    }

    public IRI getDocumentIRI(@Nonnull IRI ontologyIRI) {
        FutureTask<IRI> futureTask = new FutureTask<>(() -> {
            if(noToAll) {
                return null;
            }
            String msg = String.format(MISSING_IMPORT_MSG,
                                       ontologyIRI);
            Object resolveOption = "Yes";
            Object doNotResolveOption = "No";
            Object doNotResolveAnyOption = "No to all";
            Object[] options = {resolveOption, doNotResolveOption, doNotResolveAnyOption};
            JOptionPane optionPane = new JOptionPane(msg,
                                                     WARNING_MESSAGE,
                                                     YES_NO_OPTION,
                                                     null,
                                                     options,
                                                     resolveOption);
            JDialog dlg = optionPane.createDialog(RESOLVE_MISSING_IMPORT_TITLE);
            dlg.setVisible(true);
            Object choice = optionPane.getValue();
            if(choice.equals(doNotResolveAnyOption)) {
                this.noToAll = true;
                return null;
            }
            if(choice.equals(doNotResolveOption)) {
                return null;
            }
            UIHelper helper = new UIHelper(owlEditorKit);
            File file = helper.chooseOWLFile("Please select an ontology file");
            if(file == null) {
                return ontologyIRI;
            }
            updateActiveCatalog(ontologyIRI, file);
            return IRI.create(file);
        });

        SwingUtilities.invokeLater(futureTask);
        try {
            return futureTask.get();
        } catch(InterruptedException e) {
            logger.debug("Resolve import task interrupted");
            return null;
        } catch(ExecutionException e) {
            logger.error("An exception was thrown whilst the user was resolving a missing import: {}", e
                    .getCause()
                    .getMessage());
            return null;
        }
    }

    private void updateActiveCatalog(IRI ontologyIRI,
                                     File file) {
        OntologyCatalogManager catalogManager = owlEditorKit.getOWLModelManager().getOntologyCatalogManager();
        catalogManager.getCurrentCatalog().ifPresent(catalog -> {
            URI relativeFile = CatalogUtilities.relativize(file.toURI(), catalog);
            catalog.addEntry(0, new UriEntry("User Entered Import Resolution", catalog, ontologyIRI.toString(), relativeFile, null));
            File catalogLocation = new File(catalog.getXmlBaseContext().getXmlBase());
            try {
                CatalogUtilities.save(catalog, catalogLocation);
            } catch(IOException e) {
                logger.error("Could not save user supplied import redirection to catalog.", e);
            }
        });
    }
}

