package org.protege.editor.owl.ui.navigation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 07-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityNavPanel extends JPanel {

    private OWLEditorKit editorKit;

    private OWLEntityNavigationMediator mediator;

    private Action backAction;

    private Action forwardAction;


    public OWLEntityNavPanel(OWLEditorKit owlEditorKit) {
        this.editorKit = owlEditorKit;
        createActions();
        setLayout(new GridLayout(1, 2, 0, 0));
        add(new JButton(backAction));
        add(new JButton(forwardAction));
        mediator = new OWLEntityNavigationMediator(editorKit, backAction, forwardAction);
    }


    public void dispose() {
        mediator.dispose();
    }


    private void createActions() {

        backAction = new AbstractAction("<") {
            public void actionPerformed(ActionEvent e) {
                editorKit.getWorkspace().getOWLSelectionHistoryManager().goBack();
                editorKit.getWorkspace().displayOWLEntity(editorKit.getWorkspace().getOWLSelectionModel().getSelectedEntity());
            }
        };
        backAction.putValue(Action.SHORT_DESCRIPTION, "Back");
        KeyStroke backKS = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
                                                  Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(backKS, "nav-back");
        ActionMap actionMap = getActionMap();
        actionMap.put("nav-back", backAction);


        forwardAction = new AbstractAction(">") {
            public void actionPerformed(ActionEvent e) {
                editorKit.getWorkspace().getOWLSelectionHistoryManager().goForward();
                editorKit.getWorkspace().displayOWLEntity(editorKit.getWorkspace().getOWLSelectionModel().getSelectedEntity());
            }
        };
        forwardAction.putValue(Action.SHORT_DESCRIPTION, "Forward");
        KeyStroke forwardKS = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
                                                     Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        inputMap.put(forwardKS, "nav-forward");
        actionMap.put("nav-forward", forwardAction);
    }


    protected OWLModelManager getOWLModelManager() {
        return editorKit.getModelManager();
    }
}
