package org.protege.editor.owl.ui.ontology.imports.wizard.page;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportInfo;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;
import org.protege.editor.owl.ui.renderer.OWLOntologyCellRenderer;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LoadedOntologyPage extends OntologyImportPage {

    private static final Logger logger = Logger.getLogger(LoadedOntologyPage.class);


    public static final String ID = LoadedOntologyPage.class.getName();

    private JList ontologyList;


    public LoadedOntologyPage(OWLEditorKit owlEditorKit) {
        super(ID, "Import pre-loaded ontology", owlEditorKit);
    }


    private List<OWLOntology> getOntologies() {
        final OWLModelManager mngr = getOWLModelManager();

        List<OWLOntology> ontologies = new ArrayList<OWLOntology>(mngr.getOntologies());

        ontologies.removeAll(mngr.getOWLOntologyManager().getImportsClosure(mngr.getActiveOntology()));

        // you cannot import an ontology from the same series
        ontologies.removeAll(getOntologiesInSeries(mngr.getActiveOntology(), ontologies));

        Collections.sort(ontologies, mngr.getOWLObjectComparator());
        return ontologies;
    }


    private Set<OWLOntology> getOntologiesInSeries(OWLOntology ontology, Collection<OWLOntology> ontologies) {
        Set<OWLOntology> ontologiesInSeries = new HashSet<OWLOntology>();
        if (!ontology.getOntologyID().isAnonymous()){
            for (OWLOntology ont : ontologies){
                if (!ont.getOntologyID().isAnonymous() &&
                    ont.getOntologyID().getOntologyIRI().equals(ontology.getOntologyID().getOntologyIRI())){
                    ontologiesInSeries.add(ont);
                }
            }
        }
        return ontologiesInSeries;
    }
    
    @Override
    public void aboutToHidePanel() {
    	OntologyImportWizard wizard = (OntologyImportWizard) getWizard();
    	wizard.clearImports();
        Set<OWLOntology> sel = new HashSet<OWLOntology>();
        for (Object o : ontologyList.getSelectedValues()){
        	OWLOntology ontology = (OWLOntology) o;
        	ImportInfo parameter = new ImportInfo();
        	parameter.setOntologyID(ontology.getOntologyID());
        	parameter.setPhysicalLocation(getOWLModelManager().getOWLOntologyManager().getOntologyDocumentIRI(ontology).toURI());
        	parameter.setImportLocation(ontology.getOntologyID().getDefaultDocumentIRI());
        	wizard.addImport(parameter);
        }
    	((SelectImportLocationPage) getWizardModel().getPanel(SelectImportLocationPage.ID)).setBackPanelDescriptor(ID);
        ((ImportConfirmationPage) getWizardModel().getPanel(ImportConfirmationPage.ID)).setBackPanelDescriptor(ID);
    	super.aboutToHidePanel();
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please select an existing (pre-loaded) ontology that you want to import.");
        ontologyList = new OWLObjectList(getOWLEditorKit());
        ontologyList.setCellRenderer(new OWLOntologyCellRenderer(getOWLEditorKit()));
        ontologyList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateState();
                }
            }
        });
        parent.setLayout(new BorderLayout());
        parent.add(ComponentFactory.createScrollPane(ontologyList), BorderLayout.CENTER);
        parent.add(createCustomizedImportsComponent(), BorderLayout.SOUTH);
    }


    private void fillList() {
        ontologyList.setListData(getOntologies().toArray());
    }


    public Object getNextPanelDescriptor() {
        return getWizard().isCustomizeImports() ? SelectImportLocationPage.ID : ImportConfirmationPage.ID;
    }


    public Object getBackPanelDescriptor() {
        return ImportTypePage.ID;
    }


    public void displayingPanel() {
        fillList();
        updateState();
        ontologyList.requestFocus();
    }


    private void updateState() {
        getWizard().setNextFinishButtonEnabled(ontologyList.getSelectedValue() != null);
    }
}

