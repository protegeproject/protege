package org.protege.editor.core.ui.util;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Enumeration;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 25, 2008<br><br>
 */
public class TableUtils {


    public static void pack(JTable table, boolean packColumns, boolean packRows, int padding){
        final Enumeration<TableColumn> cols = table.getColumnModel().getColumns();

        int[] rowHeights = new int[table.getRowCount()];

        while (cols.hasMoreElements()){
            TableColumn tc = cols.nextElement();
            final int colIndex = tc.getModelIndex(); // not sure about this

            int width = 0;

            if (packColumns){
                // Get width of column header
                TableCellRenderer renderer = tc.getHeaderRenderer();
                if (renderer == null) {
                    final JTableHeader header = table.getTableHeader();
                    if (header != null){
                        renderer = header.getDefaultRenderer();
                    }
                }

                if (renderer != null){
                    Component comp = renderer.getTableCellRendererComponent(
                            table, tc.getHeaderValue(), false, false, 0, 0);
                    width = comp.getPreferredSize().width;
                }
            }

            // Check data
            for (int row=0; row<table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, colIndex);
                Component comp = renderer.getTableCellRendererComponent(
                        table, table.getValueAt(row, colIndex), false, false, row, colIndex);
                final Dimension prefSize = comp.getPreferredSize();
                if (packRows){
                    rowHeights[row] = Math.max(rowHeights[row], prefSize.height);
                }
                if (packColumns){
                    width = Math.max(width, prefSize.width);
                }
            }

            if (packColumns){
                tc.setPreferredWidth(width);
            }
        }

        if (packRows){
            for (int row=0; row<rowHeights.length; row++){
                table.setRowHeight(row, rowHeights[row] + (2*padding));
            }
        }
    }
}
