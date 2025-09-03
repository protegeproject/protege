package org.protege.editor.owl.ui.navigation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.selection.OWLSelectionHistoryManager;

import javax.swing.*;
import javax.swing.event.ChangeListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 07-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityNavigationMediator {

    private OWLEditorKit owlEditorKit;

    private Action backAction;

    private Action forwardAction;

    private ChangeListener listener;


    public OWLEntityNavigationMediator(OWLEditorKit owlEditorKit, Action backAction, Action forwardAction) {
        this.owlEditorKit = owlEditorKit;
        this.backAction = backAction;
        this.forwardAction = forwardAction;
        listener = e -> updateActionState();
        owlEditorKit.getWorkspace().getOWLSelectionHistoryManager().addChangeListener(listener);
        updateActionState();
    }


    public void dispose() {
        owlEditorKit.getWorkspace().getOWLSelectionHistoryManager().removeChangeListener(listener);
    }


    private void updateActionState() {
        OWLSelectionHistoryManager historyManager = owlEditorKit.getWorkspace().getOWLSelectionHistoryManager();
        backAction.setEnabled(historyManager.canGoBack());
        forwardAction.setEnabled(historyManager.canGoForward());
    }
}
