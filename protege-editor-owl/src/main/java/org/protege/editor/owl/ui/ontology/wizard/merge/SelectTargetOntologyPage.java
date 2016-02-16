package org.protege.editor.owl.ui.ontology.wizard.merge;

import org.protege.editor.core.ui.wizard.WizardPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 02-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class SelectTargetOntologyPage extends AbstractOWLWizardPanel {

    public static final String ID = "SelectTargetOntologyPage";

    private JList list;


    public SelectTargetOntologyPage(OWLEditorKit owlEditorKit, String title) {
        super(ID, title, owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout());
        list = new JList();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(8);
        list.setCellRenderer(getOWLEditorKit().getWorkspace().createOWLCellRenderer());
        final java.util.List<OWLOntology> orderedOntologies = new ArrayList<>(getOWLModelManager().getOntologies());
        Collections.sort(orderedOntologies, getOWLModelManager().getOWLObjectComparator());
        list.setListData(orderedOntologies.toArray());
        parent.add(new JScrollPane(list), BorderLayout.NORTH);
    }


    public Object getNextPanelDescriptor() {
        return WizardPanel.FINISH;
    }


    public void displayingPanel() {
        super.displayingPanel();
        list.requestFocus();
    }


    public OWLOntology getOntology() {
        return (OWLOntology)list.getSelectedValue();
    }
}