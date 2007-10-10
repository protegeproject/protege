package org.protege.editor.owl.ui.prefix;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PrefixTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    private JTextField textField;


    public PrefixTableCellEditor() {
        textField = new JTextField();
        textField.setFont(new JLabel().getFont());
    }


    public Object getCellEditorValue() {
        return textField.getText().trim();
    }


    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Font f = table.getCellRenderer(row, column).getTableCellRendererComponent(table,
                                                                                  "",
                                                                                  false,
                                                                                  false,
                                                                                  row,
                                                                                  column).getFont();
        textField.setFont(new Font(f.getName(), f.getStyle(), f.getSize()));
        textField.setText(value.toString());
        return textField;
    }


    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            return ((MouseEvent) e).getClickCount() == 2;
        }
        return false;
    }
}
