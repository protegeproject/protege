package org.protege.editor.core.ui.util;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
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
public class CheckTableModel<O> extends DefaultTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 836875586342144500L;


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
