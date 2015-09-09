package org.protege.editor.core.ui;

import org.protege.editor.core.OntologyRepository;
import org.protege.editor.core.OntologyRepositoryEntry;
import org.protege.editor.core.ui.util.JOptionPaneEx;

import javax.swing.*;
import java.awt.*;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 18-Oct-2008<br><br>
 */
public class OpenFromRepositoryPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -328358981641882683L;

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
