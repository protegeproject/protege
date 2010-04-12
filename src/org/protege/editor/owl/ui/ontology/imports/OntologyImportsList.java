package org.protege.editor.owl.ui.ontology.imports;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.editor.owl.model.library.folder.FolderGroupManager;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportInfo;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;
import org.protege.editor.owl.ui.renderer.OWLOntologyCellRenderer;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.UriEntry;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 28, 2009<br><br>
 */
public class OntologyImportsList extends MList {
	private static final Logger logger = Logger.getLogger(OntologyImportsList.class);
	
    private OWLEditorKit eKit;

    private OWLOntology ont;

    private MListSectionHeader directImportsHeader;

    private MListSectionHeader indirectImportsHeader;

    private OntologyImportWizard wizard;

    private OWLOntologyChangeListener ontChangeListener = new OWLOntologyChangeListener(){
        public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
            handleOntologyChanges(changes);
        }
    };

    public OntologyImportsList(OWLEditorKit eKit) {
        this.eKit = eKit;

        setCellRenderer(new OWLOntologyCellRenderer(eKit){
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof OntologyImportItem){
                    value = ((OntologyImportItem)value).getImportDeclaration().getIRI();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });


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

        eKit.getOWLModelManager().addOntologyChangeListener(ontChangeListener);
    }


    protected void handleAdd() {
        // don't need to check the section as only the direct imports can be added
    	wizard = new OntologyImportWizard((Frame) SwingUtilities.getAncestorOfClass(Frame.class, eKit.getWorkspace()), eKit);
        int ret = wizard.showModalDialog();

        if (ret == Wizard.FINISH_RETURN_CODE) {
            OWLOntologyManager manager = eKit.getModelManager().getOWLOntologyManager();
            OWLOntology activeOntology = eKit.getModelManager().getActiveOntology();
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        	for (ImportInfo importParameters : wizard.getImports()) {
        		IRI importLocation = importParameters.getImportLocation();
        		URI physicalLocation = importParameters.getPhysicalLocation();
        		if (!importLocation.equals(IRI.create(physicalLocation))) {
        		    addImportMapping(activeOntology, importLocation, IRI.create(physicalLocation));
        		}
                OWLImportsDeclaration decl = manager.getOWLDataFactory().getOWLImportsDeclaration(importLocation);
                changes.add(new AddImport(ont, decl));
                if (manager.contains(importParameters.getOntologyID())) {
                	continue;
                }
                try {
                	manager.makeLoadImportRequest(decl);
                	OWLOntology importedOnt = manager.getOntology(importParameters.getOntologyID());
                	if (importedOnt == null) {
                		logger.warn("Imported ontology has id " + importedOnt.getOntologyID() + 
                				" but during imports processing we anticipated " + importParameters.getOntologyID());
                		logger.warn("Please notify the Protege developers via the protege 4 mailing list (p4-feedback@lists.stanford.edu)");
                		continue;
                	}
                	eKit.addRecent(manager.getOntologyDocumentIRI(importedOnt).toURI());
                	eKit.getModelManager().fireEvent(EventType.ONTOLOGY_LOADED);
                }
                catch (OWLOntologyCreationException ooce) {
                	if (logger.isDebugEnabled()) { // should be handled by the loadErrorHander?
                		logger.debug("Exception caught importing ontologies", ooce);
                	}
                }
        	}
            eKit.getModelManager().applyChanges(changes);
        }
    }
    
    private void addImportMapping(OWLOntology ontology, IRI importLocation, IRI physicalLocation) {
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        manager.addIRIMapper(new SimpleIRIMapper(importLocation, physicalLocation));
        try {
            IRI importersDocumentLocation = manager.getOntologyDocumentIRI(ontology);
            if (importersDocumentLocation.getScheme().equals("file")) {
                File f = new File(importersDocumentLocation.toURI());
                XMLCatalog catalog = eKit.getModelManager().addRootFolder(f.getParentFile());
                catalog.addEntry(new UriEntry("Imports Wizard Entry", catalog, importersDocumentLocation.toURI().toString(), physicalLocation.toURI(), null));
                CatalogUtilities.save(catalog, OntologyCatalogManager.getCatalogFile(f.getParentFile()));
            }
        }
        catch (Throwable t) {
            ProtegeApplication.getErrorLog().logError(t);
        }
    }


    public void setOntology(OWLOntology ont){
        this.ont = ont;

        List<Object> data = new ArrayList<Object>();

        data.add(directImportsHeader);

        // @@TODO ordering
        for (OWLImportsDeclaration decl : ont.getImportsDeclarations()){
            data.add(new OntologyImportItem(ont, decl, eKit));
        }

        data.add(indirectImportsHeader);

        // @@TODO ordering
        try {
            for (OWLOntology ontRef : eKit.getOWLModelManager().getOWLOntologyManager().getImportsClosure(ont)) {
                if (!ontRef.equals(ont)) {
                    for (OWLImportsDeclaration dec : ontRef.getImportsDeclarations()) {
                        if (!data.contains(dec)) {
                            data.add(new OntologyImportItem(ontRef, dec, eKit));
                        }
                    }
                }
            }
        }
        catch (UnknownOWLOntologyException e) {
            throw new OWLRuntimeException(e);
        }

        setListData(data.toArray());
    }


    private void handleOntologyChanges(List<? extends OWLOntologyChange> changes) {
        for (OWLOntologyChange change : changes){
            if (change instanceof AddImport ||
                change instanceof RemoveImport){
                if (change.getOntology().equals(ont)){
                    refresh();
                    return;
                }
            }
        }
    }


    private void refresh() {
        setOntology(ont);
    }


    public void dispose(){
        eKit.getOWLModelManager().removeOntologyChangeListener(ontChangeListener);
    }
}
