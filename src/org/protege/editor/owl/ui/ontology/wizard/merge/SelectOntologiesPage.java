package org.protege.editor.owl.ui.ontology.wizard.merge;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.OWLObjectComparator;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 02-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class SelectOntologiesPage extends AbstractOWLWizardPanel {

    public static final String ID = "SelectOntologiesPage";

    private JList list;


    public SelectOntologiesPage(OWLEditorKit owlEditorKit) {
        super(ID, "Select ontologies to merge", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please select the ontologies that you want to merge into another ontology.");
        parent.setLayout(new BorderLayout());
        list = new JList();
        list.setVisibleRowCount(8);
        list.setCellRenderer(getOWLEditorKit().getOWLWorkspace().createOWLCellRenderer());
        final java.util.List<OWLOntology> orderedOntologies = new ArrayList<OWLOntology>(getOWLModelManager().getOntologies());
        Collections.sort(orderedOntologies, new OWLObjectComparator<OWLOntology>(getOWLModelManager()));
        list.setListData(orderedOntologies.toArray());
        parent.add(new JScrollPane(list), BorderLayout.NORTH);
    }


    public Object getNextPanelDescriptor() {
        return MergeTypePage.ID;
    }


    public void displayingPanel() {
        super.displayingPanel();
        list.requestFocus();
    }


    public Set<OWLOntology> getOntologies() {
        Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
        for (Object o : list.getSelectedValues()) {
            ontologies.add((OWLOntology) o);
        }
        return ontologies;
    }
}
