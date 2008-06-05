package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.selection.axioms.AbstractAxiomSelectionStrategy;
import org.protege.editor.owl.model.selection.axioms.AxiomSelectionStrategy;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLAxiom;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 20, 2008
 */
public class StrategyConstrainPanel extends AbstractOWLWizardPanel {

    public static final String ID = "StrategyConstrainPanel";

    private JList list;

    private JComponent helperPanel;

    private static final EmptyBorder EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);

    private AxiomSelectionStrategy displayStrategy = null;

    private StrategyEditorFactory editorFactory;

    private PropertyChangeListener l = new PropertyChangeListener(){
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getPropertyName().equals(AbstractAxiomSelectionStrategy.ONTOLOGIES_CHANGED)){
                helperPanel.removeAll();
                helperPanel.add(editorFactory.getEditor(displayStrategy, true).getComponent());
            }
            refresh();
        }
    };


    public StrategyConstrainPanel(OWLEditorKit eKit) {
        super(ID, "Customize you selection stratgy", eKit);
    }


    protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout());

        editorFactory = new StrategyEditorFactory(getOWLEditorKit());

        helperPanel = new JPanel(new BorderLayout());
        helperPanel.setPreferredSize(new Dimension(300, 200));
        helperPanel.setOpaque(false);

        list = new JList();
        list.setEnabled(false);
        list.setCellRenderer(new OWLCellRenderer(getOWLEditorKit()));
        JPanel axiomListPane = new JPanel(new BorderLayout(6, 6));
        axiomListPane.setOpaque(false);
        axiomListPane.add(new JLabel("Preview"), BorderLayout.NORTH);
        axiomListPane.add(new JScrollPane(list), BorderLayout.CENTER);
        JSplitPane splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                              helperPanel, axiomListPane);
        splitpane.setOpaque(false);
        splitpane.setResizeWeight(0.75);
        splitpane.setBorder(EMPTY_BORDER);
        splitpane.setBackground(Color.WHITE); // make the divider white
        parent.add(splitpane, BorderLayout.CENTER);
    }


    public void aboutToDisplayPanel() {
        AxiomSelectionStrategy selectedStrategy = getStrategy();
        if (selectedStrategy != displayStrategy){

            if (displayStrategy != null){
                displayStrategy.removePropertyChangeListener(l);
            }
            displayStrategy = selectedStrategy;
            displayStrategy.addPropertyChangeListener(l);

            setInstructions(displayStrategy.getName() +
                    ". This is a coarse grained selection" +
                    ". You will have control over individual axioms on the next page.");

            helperPanel.removeAll();

            helperPanel.add(getEditor(displayStrategy).getComponent());

            refresh();
        }
    }

    public StrategyEditor getEditor(AxiomSelectionStrategy strategy) {
        return editorFactory.getEditor(strategy, false);
    }


    private void refresh() {
        list.setListData(getSelectedAxioms().toArray());
    }


    private AxiomSelectionStrategy getStrategy() {
        return ((AxiomSelectionStrategyPanel)getWizardModel().getPanel(AxiomSelectionStrategyPanel.ID)).getSelectionStrategy();
    }


    public Object getBackPanelDescriptor() {
        return AxiomSelectionStrategyPanel.ID;
    }


    public Object getNextPanelDescriptor() {
        return AxiomSelectionPanel.ID;
    }


    public Set<OWLAxiom> getSelectedAxioms() {
        return getStrategy().getAxioms();
    }
}
