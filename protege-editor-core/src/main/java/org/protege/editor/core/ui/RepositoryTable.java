package org.protege.editor.core.ui;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.JTable;

import org.protege.editor.core.OntologyRepository;
import org.protege.editor.core.OntologyRepositoryEntry;


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 18-Oct-2008<br><br>
 */
public class RepositoryTable extends JTable {

    public RepositoryTable(OntologyRepository repository) {
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
