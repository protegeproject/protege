package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.protege.editor.owl.model.OntologyVisibilityManager;
import org.protege.editor.owl.model.OntologyVisibilityManagerImpl;
import org.protege.editor.owl.model.selection.ontologies.OntologySelectionStrategy;
import org.protege.editor.owl.model.selection.ontologies.VisibilityManagerSelectionStrategy;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.selector.OWLOntologySelectorPanel;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 6, 2008<br><br>
 */
public class VisibilityManagerOntologySelectionStrategyAction extends AbstractOntologySelectionStrategyAction {

    private OntologySelectionStrategy strategy;

    private OntologyVisibilityManager vm;

    private OWLOntologySelectorPanel ontPanel;


    public void actionPerformed(ActionEvent event) {
        if (ontPanel == null){
            ontPanel = new OWLOntologySelectorPanel(getOWLEditorKit());
        }
        int ret = new UIHelper(getOWLEditorKit()).showDialog("Select ontologies", ontPanel);
        if (ret == JOptionPane.OK_OPTION) {
            getVisibilityManager().setVisible(ontPanel.getSelectedOntologies());
            super.actionPerformed(event);
        }
    }


    protected OntologySelectionStrategy getStrategy() {
        if (strategy == null){
            strategy = new VisibilityManagerSelectionStrategy(getVisibilityManager());
        }
        return strategy;
    }


    public OntologyVisibilityManager getVisibilityManager() {
        if (vm == null){
            vm = new OntologyVisibilityManagerImpl();
        }
        return vm;
    }
}
