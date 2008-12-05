package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * User: nickdrummond Date: May 20, 2008
 */
public class SelectKitPanel extends AbstractMoveAxiomsWizardPanel {

    public static final String ID = "AxiomSelectionStrategyPanel";

    private ButtonGroup bGroup;

    private JPanel holder;

    private boolean createdButtons;


    public SelectKitPanel(OWLEditorKit editorKit) {
        super(ID, "Select method", editorKit);
        setInstructions("Select the method of choosing axioms from the source ontology.");
    }


    public void aboutToDisplayPanel() {
        createStrategyRadioButtons();
    }

    private void createStrategyRadioButtons() {
        if (!createdButtons) {
            for (final MoveAxiomsKit kit : getWizard().getMoveAxiomsKits()) {
                JRadioButton cb = new JRadioButton(new AbstractAction(kit.getName()) {
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
        return SelectSourceOntologiesPanel.ID;
    }


    public Object getNextPanelDescriptor() {
        return getWizard().getFirstPanelIDForKit();
    }
}
