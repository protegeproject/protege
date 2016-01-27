package org.protege.editor.core.ui.util;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/
public class CheckTableModel<O> extends DefaultTableModel {



    CheckTableModel(String name) {
        addColumn(CheckTable.defaultSelected, new Object[]{});
        addColumn(name, new Object[]{});
    }


    public void setData(java.util.List<O> elements, boolean selected){
        for (int i=getRowCount()-1; i>=0; i--){
            removeRow(i);
        }
        for (O element : elements){
            addRow(new Object[]{selected, element});
        }
    }


    public Class<?> getColumnClass(int col) {
        if (col == 0){
            return Boolean.class;
        }
        return super.getColumnClass(col);
    }


    public boolean isCellEditable(int row, int col) {
        return col == 0;
    }


    public List<O> getFilteredValues() {
        List<O> axioms = new ArrayList<O>();
        for (int i=0; i<getRowCount(); i++){
            if (getValueAt(i, 0).equals(Boolean.TRUE)){
                axioms.add((O)getValueAt(i, 1));
            }
        }
        return axioms;
    }


    public List<O> getAllValues() {
        List<O> values = new ArrayList<O>();
        for (int i=0; i<getRowCount(); i++){
            values.add((O)getValueAt(i, 1));
        }
        return values;
    }
}
