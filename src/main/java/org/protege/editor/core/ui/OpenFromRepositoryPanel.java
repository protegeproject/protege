package org.protege.editor.core.ui;

import org.protege.editor.core.OntologyRepository;
import org.protege.editor.core.OntologyRepositoryEntry;
import org.protege.editor.core.ui.util.JOptionPaneEx;

import javax.swing.*;
import java.awt.*;
/*
 * Copyright (C) 2008, University of Manchester
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
