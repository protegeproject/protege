package org.protege.editor.owl.ui.prefix;

import org.protege.editor.owl.ui.table.BasicOWLTable;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PrefixMapperTable extends BasicOWLTable {

    public PrefixMapperTable() {
        super(new PrefixMapperTableModel());
        setShowGrid(true);
        setRowHeight(getRowHeight() + 3);
        getColumnModel().getColumn(0).setPreferredWidth(150);
        getColumnModel().getColumn(1).setPreferredWidth(600);
        getColumnModel().getColumn(0).setCellEditor(new PrefixTableCellEditor());
        getColumnModel().getColumn(1).setCellEditor(new PrefixTableCellEditor());
    }


    public void createAndEditRow() {
        int index = getPrefixMapperTableModel().createNewMapping("");
        if (index == -1) {
            return;
        }
        editCellAt(index, 1);
    }


    protected boolean isHeaderVisible() {
        return true;
    }


    public PrefixMapperTableModel getPrefixMapperTableModel() {
        return (PrefixMapperTableModel) getModel();
    }
}
