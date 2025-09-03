package org.protege.editor.owl.ui.ontology.imports.wizard.page;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportInfo;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;
import org.protege.editor.owl.ui.renderer.OWLOntologyCellRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LoadedOntologyPage extends OntologyImportPage {
	private static final long serialVersionUID = 7719702648603699776L;

	public static final String ID = LoadedOntologyPage.class.getName();

    private JList ontologyList;


    public LoadedOntologyPage(OWLEditorKit owlEditorKit) {
        super(ID, "Import pre-loaded ontology", owlEditorKit);
    }


    private List<OWLOntology> getOntologies() {
        final OWLModelManager mngr = getOWLModelManager();

        List<OWLOntology> ontologies = new ArrayList<>(mngr.getOntologies());

        ontologies.removeAll(mngr.getOWLOntologyManager().getImportsClosure(mngr.getActiveOntology()));

        // you cannot import an ontology from the same series
        ontologies.removeAll(getOntologiesInSeries(mngr.getActiveOntology(), ontologies));

        Collections.sort(ontologies, mngr.getOWLObjectComparator());
        return ontologies;
    }


    private Set<OWLOntology> getOntologiesInSeries(OWLOntology ontology, Collection<OWLOntology> ontologies) {
        Set<OWLOntology> ontologiesInSeries = new HashSet<>();
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
    	OntologyImportWizard wizard = getWizard();
        wizard.setImportsAreFinal(false);
    	wizard.clearImports();
        for (Object o : ontologyList.getSelectedValues()){
        	OWLOntology ontology = (OWLOntology) o;
        	OWLOntologyID id = ontology.getOntologyID();
        	IRI physicalLocation = getOWLModelManager().getOWLOntologyManager().getOntologyDocumentIRI(ontology);
        	ImportInfo parameter = new ImportInfo();
        	parameter.setOntologyID(ontology.getOntologyID());
        	parameter.setPhysicalLocation(physicalLocation.toURI());
        	parameter.setImportLocation(!id.isAnonymous() ? id.getDefaultDocumentIRI().get() : physicalLocation);
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
        ontologyList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateState();
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

