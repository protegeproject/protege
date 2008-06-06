package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLObjectComparator;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.protege.editor.owl.ui.renderer.OWLOntologyCellRenderer;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LoadedOntologyPage extends AbstractImportSourcePage {

    private static final Logger logger = Logger.getLogger(LoadedOntologyPage.class);


    public static final String ID = LoadedOntologyPage.class.getName();

    private JList ontologyList;


    public LoadedOntologyPage(OWLEditorKit owlEditorKit) {
        super(ID, "Import pre-loaded ontology", owlEditorKit);
    }


    private List<OWLOntology> getOntologies() {
        List<OWLOntology> ontologies = new ArrayList<OWLOntology>();
        ontologies.addAll(getOWLModelManager().getOntologies());
        ontologies.removeAll(getOWLModelManager().getActiveOntologies());
        Collections.sort(ontologies, new OWLObjectComparator<OWLOntology>(getOWLModelManager()));
        return ontologies;
    }


    public ImportVerifier getImportVerifier() {
        return new LoadedOntologyImportVerifier(Collections.singleton((OWLOntology) ontologyList.getSelectedValue()));
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
        parent.add(ComponentFactory.createScrollPane(ontologyList));
    }


    private void fillList() {
        ontologyList.setListData(getOntologies().toArray());
    }


    public Object getNextPanelDescriptor() {
        return ImportVerificationPage.ID;
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

