package org.protege.editor.owl.ui.navigation;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLIcons;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 07-Jun-2006<br><br>
 * <p/>
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
        setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        toolBar.add(backAction);
        toolBar.add(forwardAction);
        toolBar.setBorder(null);
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        add(toolBar);
        mediator = new OWLEntityNavigationMediator(editorKit, backAction, forwardAction);
    }


    public void dispose() {
        mediator.dispose();
    }


    private void createActions() {

        backAction = new AbstractAction("Navigate back", OWLIcons.getIcon("nav.back.png")) {
            public void actionPerformed(ActionEvent e) {
                editorKit.getOWLWorkspace().getOWLSelectionHistoryManager().goBack();
                editorKit.getOWLWorkspace().displayOWLEntity(editorKit.getOWLWorkspace().getOWLSelectionModel().getSelectedEntity());
            }
        };
        KeyStroke backKS = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
                                                  Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(backKS, "nav-back");
        ActionMap actionMap = getActionMap();
        actionMap.put("nav-back", backAction);


        forwardAction = new AbstractAction("Navigate forward", OWLIcons.getIcon("nav.fwd.png")) {
            public void actionPerformed(ActionEvent e) {
                editorKit.getOWLWorkspace().getOWLSelectionHistoryManager().goForward();
                editorKit.getOWLWorkspace().displayOWLEntity(editorKit.getOWLWorkspace().getOWLSelectionModel().getSelectedEntity());
            }
        };
        KeyStroke forwardKS = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
                                                     Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        inputMap.put(forwardKS, "nav-forward");
        actionMap.put("nav-forward", forwardAction);
    }


    protected OWLModelManager getOWLModelManager() {
        return editorKit.getOWLModelManager();
    }
}
