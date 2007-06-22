package org.protege.editor.owl.ui.find;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.protege.editor.owl.ui.view.Findable;
import org.semanticweb.owl.model.OWLEntity;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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


    public OWLEntityFindPanel(OWLEditorKit owlEditorKit, Findable findable) {
        this.owlEditorKit = owlEditorKit;
        this.findable = findable;
        createUI();
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
