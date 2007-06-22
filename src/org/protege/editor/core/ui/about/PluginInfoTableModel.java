package org.protege.editor.core.ui.about;

import org.java.plugin.PluginManager;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.Version;
import org.protege.editor.core.plugin.PluginUtilities;

import javax.swing.table.AbstractTableModel;
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
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PluginInfoTableModel extends AbstractTableModel {

    private List<PluginDescriptor> descriptors;

    public static final String [] COLUMN_NAMES = new String []{"Name/ID", "Version", "Build"};


    public PluginInfoTableModel() {
        PluginManager man = PluginUtilities.getInstance().getPluginManager();
        descriptors = new ArrayList<PluginDescriptor>();
        descriptors.addAll(man.getRegistry().getPluginDescriptors());
    }


    public int getRowCount() {
        return descriptors.size();
    }


    public int getColumnCount() {
        return 3;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        PluginDescriptor descriptor = descriptors.get(rowIndex);
        Version version = descriptor.getVersion();
        if (columnIndex == 0) {
            String name = version.getName();
            if (name == null) {
                name = descriptor.getId();
            }
            return name;
        }
        else if (columnIndex == 1) {
            return version.getMajor() + "." + version.getMinor();
        }
        else {
            return version.getBuild();
        }
    }


    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
}
