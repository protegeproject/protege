package org.protege.editor.core.ui.util;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Enumeration;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

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
