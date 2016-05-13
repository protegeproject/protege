package org.protege.editor.owl.ui.ontology.imports;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportInfo;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.UriEntry;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 28, 2009<br><br>
 */
public class OntologyImportsList extends MList {

    private static final long serialVersionUID = -1532513638674168922L;

    private final Logger logger = LoggerFactory.getLogger(OntologyImportsList.class);

    private OWLEditorKit editorKit;

    private OWLOntology ont;

    private MListSectionHeader directImportsHeader;

    private MListSectionHeader indirectImportsHeader;

    private OntologyImportWizard wizard;

    private OWLOntologyChangeListener ontChangeListener = changes -> handleOntologyChanges(changes);

    public OntologyImportsList(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        setFixedCellHeight(-1);

//        setCellRenderer(new OWLOntologyCellRenderer(editorKit){
//            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//                if (value instanceof OntologyImportItem){
//                    value = ((OntologyImportItem)value).getImportDeclaration().getIRI();
//                }
//                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//            }
//        });

        setCellRenderer(new OntologyImportsItemRenderer(editorKit));

        directImportsHeader = new MListSectionHeader() {

            public String getName() {
                return "Direct Imports";
            }

            public boolean canAdd() {
                return true;
            }
        };

        indirectImportsHeader = new MListSectionHeader() {

            public String getName() {
                return "Indirect Imports";
            }

            public boolean canAdd() {
                return false;
            }
        };

        editorKit.getOWLModelManager().addOntologyChangeListener(ontChangeListener);
    }


    protected void handleAdd() {
        // don't need to check the section as only the direct imports can be added
        wizard = new OntologyImportWizard((Frame) SwingUtilities.getAncestorOfClass(Frame.class, editorKit.getWorkspace()), editorKit);
        int ret = wizard.showModalDialog();

        if (ret == Wizard.FINISH_RETURN_CODE) {
            AddImportsStrategy strategy = new AddImportsStrategy(editorKit, ont, wizard.getImports());
            strategy.addImports();
//            OWLOntologyManager manager = editorKit.getModelManager().getOWLOntologyManager();
//            OntologyCatalogManager catalogManager = editorKit.getOWLModelManager().getOntologyCatalogManager();
//            OWLOntology activeOntology = editorKit.getModelManager().getActiveOntology();
//            List<OWLOntologyChange> changes = new ArrayList<>();
//
//            for (ImportInfo importParameters : wizard.getImports()) {
//                IRI importedOntologyDocumentIRI = importParameters.getImportsDeclarationIRI();
//                URI physicalLocation = importParameters.getPhysicalLocation();
//                if (willRedirectTotheWrongPlace(catalogManager, importedOntologyDocumentIRI, physicalLocation)) {
//                    addImportMapping(activeOntology, importedOntologyDocumentIRI, IRI.create(physicalLocation));
//                }
//                OWLImportsDeclaration decl = manager.getOWLDataFactory().getOWLImportsDeclaration(importedOntologyDocumentIRI);
//                if (!manager.contains(importParameters.getOntologyID())) {
//                    try {
//                        manager.loadOntology(importedOntologyDocumentIRI);
//                        editorKit.getModelManager().fireEvent(EventType.ONTOLOGY_LOADED);
//                        if (importParameters.getOntologyID() != null && !importParameters.getOntologyID().isAnonymous()) {
//                            OWLOntology importedOnt = manager.getOntology(importParameters.getOntologyID());
//                            if (importedOnt == null) {
//                                logger.warn("Imported ontology has unexpected id. " +
//                                        "During imports processing we anticipated " + importParameters.getOntologyID());
//                                logger.warn("Please notify the Protege developers via the protege 4 mailing list (p4-feedback@lists.stanford.edu)");
//                                continue;
//                            }
//                            editorKit.addRecent(manager.getOntologyDocumentIRI(importedOnt).toURI());
//                        }
//                        changes.add(new AddImport(ont, decl));
//                    } catch (OWLOntologyCreationException e) {
//                        logger.error("There was a problem loading the ontology from {}.  Error: {}", importedOntologyDocumentIRI, e.getMessage(), e);
//                        JOptionPane.showMessageDialog(this, "An error occurred whilst the ontology at " + importedOntologyDocumentIRI + " was being loaded.", "Error loading ontology", JOptionPane.ERROR_MESSAGE);
//                    }
//                }
//
//            }
//            editorKit.getModelManager().applyChanges(changes);
        }
    }

//    private boolean willRedirectTotheWrongPlace(OntologyCatalogManager catalogManager, IRI importLocation, URI physicalLocation) {
//        if (catalogManager.getRedirect(importLocation.toURI()) == null) {
//            return !importLocation.equals(IRI.create(physicalLocation));
//        }
//        else {
//            return !physicalLocation.equals(catalogManager.getRedirect(importLocation.toURI()));
//        }
//    }
//
//    private void addImportMapping(OWLOntology ontology, IRI importLocation, IRI physicalLocation) {
//        OWLOntologyManager manager = ontology.getOWLOntologyManager();
//
//        manager.addIRIMapper(new SimpleIRIMapper(importLocation, physicalLocation));
//        IRI importersDocumentLocation = manager.getOntologyDocumentIRI(ontology);
//        if (UIUtil.isLocalFile(importersDocumentLocation.toURI())) {
//            File f = new File(importersDocumentLocation.toURI());
//            XMLCatalog catalog = editorKit.getModelManager().addRootFolder(f.getParentFile());
//            URI physicalUri = CatalogUtilities.relativize(physicalLocation.toURI(), catalog);
//            catalog.addEntry(0, new UriEntry("Imports Wizard Entry", catalog, importLocation.toURI().toString(), physicalUri, null));
//            try {
//                CatalogUtilities.save(catalog, OntologyCatalogManager.getCatalogFile(f.getParentFile()));
//            } catch (IOException e) {
//                logger.warn("An error occurred whilst saving the catalog file: {}", e);
//            }
//        }
//    }


    public void setOntology(OWLOntology ont) {
        this.ont = ont;

        List<Object> data = new ArrayList<>();

        data.add(directImportsHeader);

        // @@TODO ordering
        for (OWLImportsDeclaration decl : ont.getImportsDeclarations()) {
            data.add(new OntologyImportItem(ont, decl, editorKit));
        }

        data.add(indirectImportsHeader);

        // @@TODO ordering
        try {
            for (OWLOntology ontRef : editorKit.getOWLModelManager().getOWLOntologyManager().getImportsClosure(ont)) {
                if (!ontRef.equals(ont)) {
                    for (OWLImportsDeclaration dec : ontRef.getImportsDeclarations()) {
                        if (!data.contains(dec)) {
                            data.add(new OntologyImportItem(ontRef, dec, editorKit));
                        }
                    }
                }
            }
        } catch (UnknownOWLOntologyException e) {
            throw new OWLRuntimeException(e);
        }

        setListData(data.toArray());
    }


    private void handleOntologyChanges(List<? extends OWLOntologyChange> changes) {
        for (OWLOntologyChange change : changes) {
            if (change instanceof AddImport ||
                    change instanceof RemoveImport) {
                if (change.getOntology().equals(ont)) {
                    refresh();
                    return;
                }
            }
        }
    }


    private void refresh() {
        setOntology(ont);
    }


    public void dispose() {
        editorKit.getOWLModelManager().removeOntologyChangeListener(ontChangeListener);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }
}
