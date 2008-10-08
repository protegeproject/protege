package org.protege.editor.owl.ui.find;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.protege.editor.owl.ui.view.Findable;
import org.semanticweb.owl.model.OWLEntity;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 16-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityFindPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(OWLEntityFindPanel.class);


    private OWLEditorKit owlEditorKit;

    private Findable findable;

    private JTextField textField;

    private JList resultsList;

    Timer findTimer;


    public OWLEntityFindPanel(OWLEditorKit owlEditorKit, Findable findable) {
        this.owlEditorKit = owlEditorKit;
        this.findable = findable;
        createUI();
        findTimer = new Timer(300, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                doUpdate();
            }
        });
        findTimer.setRepeats(false);
    }


    public OWLEntity getSelectedEntity() {
        return (OWLEntity) resultsList.getSelectedValue();
    }


    private void createUI() {
        setLayout(new BorderLayout(7, 7));
        textField = new JTextField();
        JPanel textFieldPanel = new JPanel(new BorderLayout());
        textFieldPanel.add(textField);
        textFieldPanel.setBorder(ComponentFactory.createTitledBorder("Find"));
        add(textFieldPanel, BorderLayout.NORTH);
        resultsList = new OWLObjectList(owlEditorKit);
        JPanel resultsListPanel = new JPanel(new BorderLayout());
        resultsListPanel.add(ComponentFactory.createScrollPane(resultsList));
        resultsListPanel.setBorder(ComponentFactory.createTitledBorder("Results"));
        add(resultsListPanel, BorderLayout.CENTER);
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                update();
            }


            public void removeUpdate(DocumentEvent e) {
                update();
            }


            public void changedUpdate(DocumentEvent e) {
            }
        });
        textField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }
        });
    }


    private void handleKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            navUp();
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            navDown();
        }
    }


    private void navUp() {
        int selIndex = resultsList.getSelectedIndex();
        if (selIndex == -1) {
            return;
        }
        selIndex--;
        if (selIndex < 0) {
            selIndex = resultsList.getModel().getSize() - 1;
        }
        resultsList.setSelectedIndex(selIndex);
    }


    private void navDown() {
        int selIndex = resultsList.getSelectedIndex();
        if (selIndex == -1) {
            return;
        }
        selIndex++;
        if (selIndex > resultsList.getModel().getSize() - 1) {
            selIndex = 0;
        }
        resultsList.setSelectedIndex(selIndex);
    }


    private void update() {
        findTimer.restart();
    }


    private void doUpdate() {
        String text = textField.getText().trim();
        if (text.length() == 0) {
            resultsList.setListData(new Object [0]);
            return;
        }
        List<? extends OWLEntity> result = findable.find(text);
        resultsList.setListData(result.toArray());
        if (!result.isEmpty()) {
            resultsList.setSelectedIndex(0);
        }
    }


    public Dimension getPreferredSize() {
        return new Dimension(250, 400);
    }


    public static OWLEntity showDialog(JComponent parent, OWLEditorKit owlEditorKit, Findable findable) {
        final OWLEntityFindPanel panel = new OWLEntityFindPanel(owlEditorKit, findable);
        int ret = JOptionPaneEx.showConfirmDialog(parent,
                                                  "Find",
                                                  panel,
                                                  JOptionPane.PLAIN_MESSAGE,
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  panel.textField);
        if (ret == JOptionPane.OK_OPTION) {
            return panel.getSelectedEntity();
        }
        else {
            return null;
        }
    }
}
