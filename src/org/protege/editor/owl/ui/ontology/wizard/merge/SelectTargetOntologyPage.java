package org.protege.editor.owl.ui.ontology.wizard.merge;

import org.protege.editor.core.ui.wizard.WizardPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import java.awt.*;


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


    public SelectTargetOntologyPage(OWLEditorKit owlEditorKit) {
        super(ID, "Select ontology to merge into", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please select the target ontology to merge into");
        parent.setLayout(new BorderLayout());
        list = new JList();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(8);
        list.setCellRenderer(getOWLEditorKit().getOWLWorkspace().createOWLCellRenderer());
        list.setListData(getOWLModelManager().getOntologies().toArray());
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