package org.protege.editor.core.ui;

/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.protege.editor.core.OntologyRepository;
import org.protege.editor.core.OntologyRepositoryEntry;
import org.protege.editor.core.ui.util.JOptionPaneEx;


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 18-Oct-2008<br><br>
 */
public class OpenFromRepositoryPanel extends JPanel {

    private OntologyRepository repository;

    private RepositoryTable table;

    public OpenFromRepositoryPanel(OntologyRepository repository) {
        this.repository = repository;
        createUI();
    }

    private void createUI() {
        setLayout(new BorderLayout());
        table = new RepositoryTable(repository);
        add(new JScrollPane(table));
    }


    public Dimension getPreferredSize() {
        return new Dimension(800, 400);
    }


    public static OntologyRepositoryEntry showDialog(OntologyRepository repository) {
        repository.refresh();
        OpenFromRepositoryPanel panel = new OpenFromRepositoryPanel(repository);
        int ret = JOptionPaneEx.showConfirmDialog(null, "Open from " + repository.getName(), panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, panel.table);
        if(ret == JOptionPane.OK_OPTION) {
            return panel.table.getSelectedEntry();
        }
        return null;
    }

}
