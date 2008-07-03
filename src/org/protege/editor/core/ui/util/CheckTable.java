package org.protege.editor.core.ui.util;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 2, 2008<br><br>
 *
 * Implements a two column table where the left column is a list of checkboxes and the right is a list of
 * objects of type O (provided by <code>setData()</code>).
 * The left column has a check/uncheck all checkbox at the top.
 * A list of checked items can be retrieved easily using <code>getFilteredValues</code>
 */
public class CheckTable<O> extends JTable {

    public static Boolean defaultSelected = false;

    private static final int MARGIN_SIZE = 2;

    private JCheckBox checkAllCheckbox;

    private TableCellRenderer renderer;

    private boolean refreshRowHeight = true;

    private List<ListSelectionListener> checkSelListeners = new ArrayList<ListSelectionListener>();

    private DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer(){
        public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            if (col == 0){
                return checkAllCheckbox;
            }
            return super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, col);
        }
    };

    private MouseAdapter checkAllMouseListener = new MouseAdapter(){
        public boolean mousePressed = false;


        public void mouseClicked(MouseEvent e) {
            if (mousePressed){
                mousePressed = false;
                JTableHeader header = (JTableHeader)(e.getSource());
                JTable tableView = header.getTable();
                TableColumnModel columnModel = tableView.getColumnModel();
                int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                int column = tableView.convertColumnIndexToModel(viewColumn);

                if (viewColumn == column && e.getClickCount() == 1 && column == 0) {
                    checkAllCheckbox.doClick();
                }
                header.repaint();
            }
        }


        public void mousePressed(MouseEvent event) {
            mousePressed = true;
        }
    };


    private ChangeListener checkAllActionListener = new ChangeListener(){
        public void stateChanged(ChangeEvent event) {
            for (int i=0; i<getModel().getRowCount(); i++){
                getModel().setValueAt(checkAllCheckbox.isSelected(), i, 0);
            }
            notifyCheckSelectionChanged();
        }
    };

    private CellEditorListener checkEditorListener = new CellEditorListener(){

        public void editingStopped(ChangeEvent event) {
            notifyCheckSelectionChanged();
        }

        public void editingCanceled(ChangeEvent event) {
            // do nothing
        }
    };


    public CheckTable(String name) {
        setModel(new CheckTableModel<O>(name));

        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        final JTableHeader header = getTableHeader();
        header.setReorderingAllowed(false);
        header.addMouseListener(checkAllMouseListener);

        checkAllCheckbox = new JCheckBox();
        checkAllCheckbox.setSelected(defaultSelected);
        checkAllCheckbox.addChangeListener(checkAllActionListener);

        final TableColumn checkCol = getColumnModel().getColumn(0);
        checkCol.setMaxWidth(checkAllCheckbox.getPreferredSize().width + 2);

        checkCol.setHeaderRenderer(headerRenderer);
        getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "checkSelection");
        getActionMap().put("checkSelection", new AbstractAction(){
            public void actionPerformed(ActionEvent event) {
                checkSelection();
            }
        });
        
        getDefaultEditor(Boolean.class).addCellEditorListener(checkEditorListener);
    }


    public void checkAll(boolean b) {
        checkAllCheckbox.setSelected(b);
        revalidate();
    }


    public void validate() {
        if (refreshRowHeight){
            packRows();
            refreshRowHeight = false;
        }
        super.validate();
    }


    public void revalidate() {
        refreshRowHeight = true;
        super.revalidate();
    }


    public void packRows() {
        for (int r=0; r<getRowCount(); r++) {
            // Get the preferred height
            int h = getPreferredRowHeight(r);

            // Now set the row height using the preferred height
            if (getRowHeight(r) != h) {
                setRowHeight(r, h);
            }
        }
    }


    private int getPreferredRowHeight(int r) {
        // Get the current default height for all rows
        int height = getRowHeight();

        // Determine highest cell in the row
        for (int c=0; c<getColumnCount(); c++) {
            TableCellRenderer renderer = getCellRenderer(r, c);
            Component comp = prepareRenderer(renderer, r, c);
            int h = comp.getPreferredSize().height + 2*MARGIN_SIZE;
            height = Math.max(height, h);
        }
        return height;
    }


    public void setDefaultRenderer(TableCellRenderer renderer) {
        this.renderer = renderer;
    }


    public TableCellRenderer getDefaultRenderer(Class<?> aClass) {
        if (renderer == null || aClass.equals(Boolean.class)){
            return super.getDefaultRenderer(aClass);
        }
        return renderer;
    }


    public void setData(java.util.List<O> elements) {
        ((CheckTableModel)getModel()).setData(elements, checkAllCheckbox.isSelected());
        packRows();
    }


    public List<O> getFilteredValues() {
        return ((CheckTableModel)getModel()).getFilteredValues();
    }


    /**
     * If all values are checked, uncheck them.
     * If any of them are not selected, select all.
     */
    private void checkSelection() {
        boolean value = false;

        for (int row : getSelectedRows()){
            if (getValueAt(row, 0).equals(Boolean.FALSE)){
                value = true;
            }
        }
        for (int row : getSelectedRows()){
            setValueAt(value, row, 0);
        }
        notifyCheckSelectionChanged();
    }

    
    public void addCheckSelectionListener(ListSelectionListener l) {
        checkSelListeners.add(l);
    }


    public void removeCheckSelectionListener(ListSelectionListener l){
        checkSelListeners.remove(l);
    }


    private void notifyCheckSelectionChanged() {
        int min = -1;
        int max = -1;
        for (int i=0; i<getRowCount(); i++){
            if (getValueAt(i, 0).equals(Boolean.TRUE)){
                if (min == -1){
                    min = i;
                }
                max = i;
            }
        }

        for (ListSelectionListener l : checkSelListeners){
            l.valueChanged(new ListSelectionEvent(CheckTable.this, min, max, false));
        }
    }


    /**
     * Only to be used with the access methods provided
     */
    class CheckTableModel<O> extends DefaultTableModel{

        CheckTableModel(String name) {
            addColumn(CheckTable.defaultSelected, new Object[]{});
            addColumn(name, new Object[]{});
        }


        void setData(java.util.List<O> elements, boolean selected){
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


        List<O> getFilteredValues() {
            List<O> axioms = new ArrayList<O>();
            for (int i=0; i<getRowCount(); i++){
                if (getValueAt(i, 0).equals(Boolean.TRUE)){
                    axioms.add((O)getValueAt(i, 1));
                }
            }
            return axioms;
        }
    }
}