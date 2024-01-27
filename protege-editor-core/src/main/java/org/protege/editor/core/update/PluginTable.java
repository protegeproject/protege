package org.protege.editor.core.update;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.core.ui.util.TableUtils;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 5, 2008<br><br>
 */
public class PluginTable extends JPanel {


    private JTable table;

    private PluginUpdateTableModel tableModel;

    private List<PluginInfo> provider;

    private List<ListSelectionListener> pendingListeners = new ArrayList<>();

    private ComponentAdapter componentAdapter = new ComponentAdapter(){
        public void componentShown(ComponentEvent event) {
            removeComponentListener(componentAdapter);
            handleTableShown();
        }
    };

    private JLabel waitLabel;


    public PluginTable(List<PluginInfo> plugins) {
        setOpaque(false);
        this.provider = new ArrayList<>(plugins);
        addComponentListener(componentAdapter);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(500, 200));
        waitLabel = new JLabel("Checking for plugins...", Icons.getIcon("busy.gif"), SwingConstants.CENTER);
        add(waitLabel, BorderLayout.CENTER);
        handleTableShown();
    }


    private void handleTableShown(){

        Thread t = new Thread(() -> {
            tableModel = new PluginUpdateTableModel(provider);
            table = new JTable(tableModel);

            table.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            table.setShowGrid(true);
            table.setRowMargin(1);
            table.setGridColor(new Color(220, 220, 220));
            table.setShowVerticalLines(false);
            table.setRowHeight(table.getRowHeight() + 5);
            table.setRowSelectionAllowed(true);
            table.setColumnSelectionAllowed(false);

            TableUtils.pack(table, true, false, 1);

            final JScrollPane tableSp = new JScrollPane(table);

            for (ListSelectionListener l : pendingListeners){
                table.getSelectionModel().addListSelectionListener(l);
            }
            pendingListeners.clear();

            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    remove(waitLabel);
                    add(tableSp, BorderLayout.CENTER);
                    validate();
                }
            });
        }, "Load plugin table contents");

        t.start();
    }


    public List<PluginInfo> getSelectedUpdateInfo() {
        if (tableModel != null){
            return tableModel.getSelectedUpdateInfo();
        }
        return Collections.emptyList();
    }


    public void addListSelectionListener(ListSelectionListener l){
        if (table == null){
            pendingListeners.add(l);
        }
        else{
            table.getSelectionModel().addListSelectionListener(l);
        }
    }


    public void removeListSelectionListener(ListSelectionListener l){
        table.getSelectionModel().removeListSelectionListener(l);
    }


    public PluginInfo getCurrentUpdateInfo() {
        if (table != null && table.getSelectedRow() >= 0){
            return tableModel.getUpdateInfoAt(table.getSelectedRow());
        }
        return null;
    }


    private class PluginUpdateTableModel extends AbstractTableModel {


        private List<Boolean> install;

        private final String[] colNames = {"Install", "Name", "Current version", "Available version"};

        private List<PluginInfo> plugins;


        public PluginUpdateTableModel(List<PluginInfo> plugins) {
            this.plugins = new ArrayList<>(plugins);
            getInstallList();
        }


        public List<Boolean> getInstallList(){
            if (install == null){
                install = new ArrayList<>(plugins.size());
                for (PluginInfo info : plugins) {
                    install.add(false);//provider.isSelected(info));
                }
            }
            return install;
        }

//        public List<PluginInfo> getUpdateInfoList() {
//            return new ArrayList<>(plugins);
//        }


        public PluginInfo getUpdateInfoAt(int index) {
            return plugins.get(index);
        }


        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class;
            }
            return super.getColumnClass(columnIndex);
        }


        public List<PluginInfo> getSelectedUpdateInfo() {
            List<PluginInfo> sel = new ArrayList<>();
            int counter = 0;
            for (Boolean b : getInstallList()) {
                if (b) {
                    sel.add(plugins.get(counter));
                }
                counter++;
            }
            return sel;
        }


        public int getColumnCount() {
            return colNames.length;
        }


        public int getRowCount() {
            return plugins.size();
        }


        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return getInstallList().get(rowIndex);
            }
            else {
                final PluginInfo info = plugins.get(rowIndex);
                if (columnIndex == 1) {
                    if (info.getPluginDescriptor() != null){
                        return info.getPluginDescriptor().getHeaders().get("Bundle-Name");
                    }
                    return info.getLabel();
                }
                else if (columnIndex == 2) {
                    Bundle bundle = info.getPluginDescriptor();
                    StringBuilder versionString = new StringBuilder();
                    if (bundle != null){
                        Version version = PluginUtilities.getBundleVersion(bundle);
                        versionString.append(version.getMajor());
                        versionString.append(".");
                        versionString.append(version.getMinor());
                        versionString.append(".");
                        versionString.append(version.getMicro());
                        
                        String qualifier = version.getQualifier();
                        if ((qualifier != null) && (!qualifier.isEmpty())){
                            versionString.append(".");
                            versionString.append(qualifier);
                        }
                    }
                    return versionString;
                }
                else {
                    return info.getAvailableVersion();
                }
            }
        }


        public String getColumnName(int column) {
            return colNames[column];
        }


        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }


        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                if (aValue instanceof Boolean) {
                    getInstallList().set(rowIndex, (Boolean) aValue);
                }
            }
        }
    }
}
