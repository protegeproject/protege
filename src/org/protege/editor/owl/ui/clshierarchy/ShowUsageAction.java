package org.protege.editor.owl.ui.clshierarchy;

import java.awt.event.ActionEvent;

import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.view.OWLSelectionViewAction;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 29-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ShowUsageAction extends OWLSelectionViewAction {

    private OWLWorkspace workspace;


    public ShowUsageAction(OWLWorkspace workspace) {
        super("Show usage", OWLIcons.getIcon("class.usage.png"));
        this.workspace = workspace;
    }


    public void updateState() {
        setEnabled(true);
    }


    public void dispose() {
    }


    public void actionPerformed(ActionEvent e) {
        workspace.showResultsView("OWLClassUsageView", false, Workspace.BOTTOM_RESULTS_VIEW);
    }
}
