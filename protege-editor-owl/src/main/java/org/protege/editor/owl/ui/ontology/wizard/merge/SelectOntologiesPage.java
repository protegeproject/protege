package org.protege.editor.owl.ui.ontology.wizard.merge;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 02-Jul-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class SelectOntologiesPage extends AbstractOWLWizardPanel {

    public static final String ID = "SelectOntologiesPage";

    private JList list;


    public SelectOntologiesPage(OWLEditorKit owlEditorKit, String title) {
        super(ID, title, owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout());
        list = new JList();
        list.setVisibleRowCount(8);
        list.setCellRenderer(getOWLEditorKit().getWorkspace().createOWLCellRenderer());
        final java.util.List<OWLOntology> orderedOntologies = new ArrayList<>(getOWLModelManager().getOntologies());
        Collections.sort(orderedOntologies, getOWLModelManager().getOWLObjectComparator());
        list.setListData(orderedOntologies.toArray());
        parent.add(new JScrollPane(list), BorderLayout.NORTH);
    }


    public Object getNextPanelDescriptor() {
        return MergeTypePage.ID;
    }


    public void displayingPanel() {
        super.displayingPanel();
        if (list.getSelectedValue() == null){
            Set<OWLOntology> defOnts = getDefaultOntologies();
            for (int i=0; i<list.getModel().getSize(); i++){
                if (defOnts.contains(list.getModel().getElementAt(i))){
                    list.addSelectionInterval(i, i);
                }
            }
        }
        list.requestFocus();
    }

    /**
     * Override to set the ontologies that are first shown
     * @return the set of ontologies that should be selected the first
     * time this page is shown
     */
    protected Set<OWLOntology> getDefaultOntologies() {
        return Collections.singleton(getOWLModelManager().getActiveOntology());
    }


    public Set<OWLOntology> getOntologies() {
        Set<OWLOntology> ontologies = new HashSet<>();
        for (Object o : list.getSelectedValues()) {
            ontologies.add((OWLOntology) o);
        }
        return ontologies;
    }
}
