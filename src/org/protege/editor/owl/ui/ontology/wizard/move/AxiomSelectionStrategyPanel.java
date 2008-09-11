package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.selection.axioms.*;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.ontology.wizard.merge.SelectOntologiesPage;
import org.protege.editor.core.ProtegeApplication;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: nickdrummond Date: May 20, 2008
 */
public class AxiomSelectionStrategyPanel extends AbstractMoveAxiomsWizardPanel {

    public static final String ID = "AxiomSelectionStrategyPanel";

    private ButtonGroup bGroup;

    private JPanel holder;

    private boolean createdButtons;


    public AxiomSelectionStrategyPanel(OWLEditorKit editorKit) {
        super(ID, "Axiom selection strategy", editorKit);
        setInstructions("Please choose an axiom selection strategy." + " This is a coarse grained way of describing the way in which you wish to add axioms to the set that will be moved." + " You will get more fine grained control in the following steps.");
    }


    public void aboutToDisplayPanel() {
        createStrategyRadioButtons();
    }

    private void createStrategyRadioButtons() {
        if (!createdButtons) {
            for (final MoveAxiomsKit kit : getWizard().getMoveAxiomsKits()) {
                final AxiomSelectionStrategy strategy = kit.getAxiomSelectionStrategy();
                JRadioButton cb = new JRadioButton(new AbstractAction(strategy.getName()) {
                    public void actionPerformed(ActionEvent actionEvent) {
                        getWizard().setSelectedKit(kit);
                    }
                });
                cb.setOpaque(false);
                cb.setSelected(getWizard().getSelectedKit() == kit);
                bGroup.add(cb);
                holder.add(cb);
            }
            createdButtons = true;
        }
    }


    protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout());
        holder = new JPanel();
        holder.setOpaque(false);
        holder.setLayout(new BoxLayout(holder, BoxLayout.PAGE_AXIS));
        bGroup = new ButtonGroup();
        parent.add(holder, BorderLayout.NORTH);
    }


    public Object getBackPanelDescriptor() {
        return SelectSourceOntologiesPage.ID;
    }


    public Object getNextPanelDescriptor() {
        return StrategyConstrainPanel.ID;
    }
}
