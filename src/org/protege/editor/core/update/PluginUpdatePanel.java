package org.protege.editor.core.update;



import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Jun-2007<br><br>
 */
public class PluginUpdatePanel extends JPanel {

    private JTable table;

    private PluginUpdateTableModel tableModel;

    private JEditorPane editorPane;

    private Map<UpdateInfo, String> updateInfoReadmeMap;


    public PluginUpdatePanel(List<UpdateInfo> updateInfoList) {
        setLayout(new BorderLayout(2, 2));
        add(new JLabel("Plugin updates are available:"), BorderLayout.NORTH);
        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        sp.setResizeWeight(0.5);
        table = new JTable(tableModel = new PluginUpdateTableModel(updateInfoList));
        table.setShowGrid(true);
        table.setRowMargin(1);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setRowHeight(table.getRowHeight() + 5);
        JScrollPane tableSp = new JScrollPane(table);
        tableSp.setPreferredSize(new Dimension(450, 200));
        sp.setTopComponent(tableSp);
        editorPane = new JEditorPane("text/html", "");
        editorPane.setPreferredSize(new Dimension(300, 200));
        editorPane.setEditable(false);
        editorPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        sp.setBottomComponent(new JScrollPane(editorPane));
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateReadme();
            }
        });
        add(sp);
        sp.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        updateInfoReadmeMap = new HashMap<UpdateInfo, String>();
    }


    private void updateReadme() {
        int selRow = table.getSelectedRow();
        if (selRow == -1) {
            return;
        }
        UpdateInfo info = tableModel.getUpdateInfoAt(selRow);
        String readme = updateInfoReadmeMap.get(info);

        if (readme == null) {
            URI readmeURI = info.getReadmeURI();
            if (readmeURI == null) {
                readme = "";
                updateInfoReadmeMap.put(info, readme);
            }
            else {
                try {
                    URL url = readmeURI.toURL();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(url.openStream())));
                    String line;
                    StringBuilder readmeBuilder = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        readmeBuilder.append(line);
                        readmeBuilder.append("\n");
                    }
                    reader.close();
                    readme = readmeBuilder.toString();
                }
                catch (IOException e) {
                    readme = "Problem obtaining details: " + e.getMessage();
                }
            }
        }
        editorPane.setText(readme);
    }


    public List<UpdateInfo> getPluginsToInstall() {
        return tableModel.getSelectedUpdateInfo();
    }


    private class PluginUpdateTableModel extends AbstractTableModel {

        private List<Boolean> install;

        private List<UpdateInfo> updateInfoList;


        public PluginUpdateTableModel(List<UpdateInfo> updateInfoList) {
            this.updateInfoList = updateInfoList;
            install = new ArrayList<Boolean>(updateInfoList.size());
            for (int i = 0; i < updateInfoList.size(); i++) {
                install.add(true);
            }
        }


        public UpdateInfo getUpdateInfoAt(int index) {
            return updateInfoList.get(index);
        }


        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class;
            }
            return super.getColumnClass(columnIndex);
        }


        public List<UpdateInfo> getSelectedUpdateInfo() {
            List<UpdateInfo> sel = new ArrayList<UpdateInfo>();
            int counter = 0;
            for (Boolean b : install) {
                if (b) {
                    sel.add(updateInfoList.get(counter));
                }
                counter++;
            }
            return sel;
        }


        public int getColumnCount() {
            return 4;
        }


        public int getRowCount() {
            return updateInfoList.size();
        }


        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return install.get(rowIndex);
            }
            else if (columnIndex == 1) {
                return updateInfoList.get(rowIndex).getPluginDescriptor().getVersion().getName();
            }
            else if (columnIndex == 2) {
                Version version = updateInfoList.get(rowIndex).getPluginDescriptor().getVersion();
                StringBuilder versionString = new StringBuilder();
                versionString.append(version.getMajor());
                versionString.append(".");
                versionString.append(version.getMinor());
                versionString.append(".");
                versionString.append(version.getBuild());
                return versionString;
            }
            else {
                return updateInfoList.get(rowIndex).getAvailableVersion();
            }
        }


        public String getColumnName(int column) {
            if (column == 0) {
                return "Install";
            }
            else if (column == 1) {
                return "Name";
            }
            else if (column == 2) {
                return "Current version";
            }
            else {
                return "Available version";
            }
        }


        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }


        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                if (aValue instanceof Boolean) {
                    install.set(rowIndex, (Boolean) aValue);
                }
            }
        }
    }


    public static List<UpdateInfo> showDialog(List<UpdateInfo> availableUpdates) {
        PluginUpdatePanel panel = new PluginUpdatePanel(availableUpdates);
        Object [] options = new String []{"Install", "Not now"};
        int ret = JOptionPane.showOptionDialog(null,
                                               panel,
                                               "Automatic Update",
                                               JOptionPane.OK_CANCEL_OPTION,
                                               JOptionPane.PLAIN_MESSAGE,
                                               null,
                                               options,
                                               options[0]);
        if (ret == 0) {
            return panel.getPluginsToInstall();
        }
        return null;
    }
}
