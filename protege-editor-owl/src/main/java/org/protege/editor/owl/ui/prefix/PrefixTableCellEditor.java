package org.protege.editor.owl.ui.prefix;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PrefixTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    private JTextField textField;


    public PrefixTableCellEditor() {
        textField = new JTextField();
        textField.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if(textField.getCaretPosition() == -1) {
                    textField.selectAll();
                }
            }
        });
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
        return true;
    }
}
