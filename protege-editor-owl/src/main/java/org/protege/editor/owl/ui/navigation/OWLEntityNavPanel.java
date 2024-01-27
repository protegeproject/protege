package org.protege.editor.owl.ui.navigation;

import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;


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
        setLayout(new GridLayout(1, 2, 5, 0));
        JButton backButton = new JButton(backAction);
        add(styleButton(backButton));
        add(styleButton(new JButton(forwardAction)));
        mediator = new OWLEntityNavigationMediator(editorKit, backAction, forwardAction);
    }

    private static JButton styleButton(JButton button) {
        button.setBackground(null);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }


    public void dispose() {
        mediator.dispose();
    }


    private void createActions() {

        backAction = new AbstractAction("", new NavIcon(NavIcon.Direction.BACK)) {
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


        forwardAction = new AbstractAction("", new NavIcon(NavIcon.Direction.FORWARD)) {
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
