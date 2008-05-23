package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.selection.axioms.*;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.ontology.wizard.merge.SelectOntologiesPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * User: nickdrummond
 * Date: May 20, 2008
 */
public class AxiomSelectionStrategyPanel extends AbstractOWLWizardPanel {

    public static final String ID = "AxiomSelectionStrategyPanel";

    private ButtonGroup bGroup;

    private List<AxiomSelectionStrategy> strategies = new ArrayList<AxiomSelectionStrategy>();

    private AxiomSelectionStrategy currentStrategy;

    private JPanel holder;


    public AxiomSelectionStrategyPanel(OWLEditorKit editorKit) {
        super(ID, "Axiom selection strategy", editorKit);
        setInstructions("Please choose an axiom selection strategy." +
                " This is a course grained way of describing the way in which you wish to add axioms to the set that will be moved." +
                " You will get more fine grained control in the following steps.");

        // @@TODO - persist previous strategy (would need to rename it to be useful)

            currentStrategy = new ClassReferencingAxiomsStrategy();
            registerStrategy(currentStrategy);

        registerStrategy(new ObjectPropertyReferencingAxiomStrategy());
        registerStrategy(new DataPropertyReferencingAxiomStrategy());
        registerStrategy(new IndividualReferencingAxiomStrategy());
        registerStrategy(new AnnotationAxiomsStrategy());
        registerStrategy(new AxiomTypeStrategy());
        registerStrategy(new AllAxiomsStrategy());
    }

    private void registerStrategy(final AxiomSelectionStrategy strategy) {
        strategies.add(strategy);

        JCheckBox cb = new JCheckBox(new AbstractAction(strategy.getName()){
            public void actionPerformed(ActionEvent actionEvent) {
                currentStrategy = strategy;
            }
        });

        cb.setOpaque(false);
        cb.setSelected(strategy.equals(currentStrategy));
        bGroup.add(cb);
        holder.add(cb);
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
        return SelectOntologiesPage.ID;
    }
    
    public Object getNextPanelDescriptor() {
        return AxiomSelectionPanel.ID;
    }

    public AxiomSelectionStrategy getSelectionStrategy() {
        return currentStrategy;
    }
}
