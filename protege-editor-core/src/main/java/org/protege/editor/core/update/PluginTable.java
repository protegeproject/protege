package org.protege.editor.core.update;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.core.ui.util.TableUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
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

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 5, 2008<br><br>
 */
public class PluginTable extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 6305532687611320179L;

    private JTable table;

    private PluginUpdateTableModel tableModel;

    private List<PluginInfo> provider;

    private List<ListSelectionListener> pendingListeners = new ArrayList<ListSelectionListener>();

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
    }


    private void handleTableShown(){

        Thread t = new Thread(new Runnable(){
            public void run() {
                tableModel = new PluginUpdateTableModel(provider);
                table = new JTable(tableModel);

                table.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
                table.setShowGrid(true);
                table.setRowMargin(1);
                table.setGridColor(Color.LIGHT_GRAY);
                table.setRowHeight(table.getRowHeight() + 5);
                table.setRowSelectionAllowed(true);
                table.setColumnSelectionAllowed(false);
                TableUtils.pack(table, true, false, 3);
                
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
            }
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

        /**
         * 
         */
        private static final long serialVersionUID = 7766791162497899167L;

        private List<Boolean> install;

        private final String[] colNames = {"Install", "Name", "Current version", "Available version"};

        private List<PluginInfo> plugins;


        public PluginUpdateTableModel(List<PluginInfo> plugins) {
            this.plugins = new ArrayList<>(plugins);
            getInstallList();
        }


        public List<Boolean> getInstallList(){
            if (install == null){
                install = new ArrayList<Boolean>(plugins.size());
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
            List<PluginInfo> sel = new ArrayList<PluginInfo>();
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
