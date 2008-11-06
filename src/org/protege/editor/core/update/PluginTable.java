package org.protege.editor.core.update;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.protege.editor.core.plugin.PluginUtilities;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 5, 2008<br><br>
 */
public class PluginTable extends JPanel {

    private JTable table;

    private PluginUpdateTableModel tableModel;

    private DownloadsProvider provider;

    private List<ListSelectionListener> pendingListeners = new ArrayList<ListSelectionListener>();

    private ComponentAdapter componentAdapter = new ComponentAdapter(){
        public void componentShown(ComponentEvent event) {
            createTree();
        }
    };


    public PluginTable(DownloadsProvider provider) {
        this.provider = provider;
        addComponentListener(componentAdapter);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(500, 200));
    }


    private void createTree(){
        table = new JTable(tableModel = new PluginUpdateTableModel(provider));
        table.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        table.setShowGrid(true);
        table.setRowMargin(1);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setRowHeight(table.getRowHeight() + 5);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);

        JScrollPane tableSp = new JScrollPane(table);

        for (ListSelectionListener l : pendingListeners){
            table.getSelectionModel().addListSelectionListener(l);
        }
        pendingListeners.clear();

        add(tableSp, BorderLayout.CENTER);
        validate();

        removeComponentListener(componentAdapter);
    }


    public List<PluginInfo> getSelectedUpdateInfo() {
        return tableModel.getSelectedUpdateInfo();
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
        if (table != null){
            return tableModel.getUpdateInfoAt(table.getSelectedRow());
        }
        return null;
    }


    private class PluginUpdateTableModel extends AbstractTableModel {

        private List<Boolean> install;

        private final String[] colNames = {"Install", "Name", "Current version", "Available version"};

        private DownloadsProvider provider;


        public PluginUpdateTableModel(DownloadsProvider provider) {
            this.provider = provider;
        }


        public List<Boolean> getInstallList(){
            if (install == null){
                install = new ArrayList<Boolean>(getUpdateInfoList().size());
                for (PluginInfo info : getUpdateInfoList()) {
                    install.add(provider.isSelected(info));
                }
            }
            return install;
        }

        public List<PluginInfo> getUpdateInfoList() {
            return provider.getAvailableDownloads();
        }


        public PluginInfo getUpdateInfoAt(int index) {
            return getUpdateInfoList().get(index);
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
                    sel.add(getUpdateInfoList().get(counter));
                }
                counter++;
            }
            return sel;
        }


        public int getColumnCount() {
            return colNames.length;
        }


        public int getRowCount() {
            return getUpdateInfoList().size();
        }


        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return getInstallList().get(rowIndex);
            }
            else {
                final PluginInfo info = getUpdateInfoList().get(rowIndex);
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
                        if (version.getQualifier() != null){
                            versionString.append(".");
                            versionString.append(version.getQualifier());
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
