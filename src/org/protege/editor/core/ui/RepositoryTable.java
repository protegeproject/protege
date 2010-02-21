package org.protege.editor.core.ui;

import org.protege.editor.core.OntologyRepository;
import org.protege.editor.core.OntologyRepositoryEntry;

import javax.swing.*;
import java.net.URI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
public class RepositoryTable extends JTable {

    /**
     * 
     */
    private static final long serialVersionUID = 343836249221539974L;
    private OntologyRepository repository;


    public RepositoryTable(OntologyRepository repository) {
        this.repository = repository;
        setModel(new RepositoryTableModel(repository));
        setRowHeight(getRowHeight() + 4);
        setShowHorizontalLines(true);
        setGridColor(Color.LIGHT_GRAY);
        getColumnModel().getColumn(0).setPreferredWidth(100);
        getTableHeader().addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                sort(e);
            }
        }
    );}


    private void sort(MouseEvent e) {
        int col = getTableHeader().columnAtPoint(e.getPoint());
        if(col == -1) {
            return;
        }
        ((RepositoryTableModel) getModel()).sortByColumn(col);
    }

    public URI getSelectedOntologyURI() {
        return (URI) ((RepositoryTableModel) getModel()).getValueAt(getSelectedRow(), RepositoryTableModel.ONTOLOGY_URI_COL);
    }


    public OntologyRepositoryEntry getSelectedEntry() {
        return ((RepositoryTableModel) getModel()).getEntryAt(getSelectedRow());
    }
}
