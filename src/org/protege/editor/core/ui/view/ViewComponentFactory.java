package org.protege.editor.core.ui.view;

import org.coode.mdock.ComponentFactory;
import org.protege.editor.core.ui.workspace.Workspace;

import javax.swing.*;
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
 * Medical Informatics Group<br>
 * Date: 26-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ViewComponentFactory implements ComponentFactory {

    private Workspace workspace;


    public ViewComponentFactory(Workspace workspace) {
        this.workspace = workspace;
    }


    public JComponent createComponent(Map<String, String> properties) {
        String pluginId = properties.get("pluginId");
        ViewComponentPluginLoader loader = new ViewComponentPluginLoader(workspace);
        for (ViewComponentPlugin plugin : loader.getPlugins()) {
            if (plugin.getId().equals(pluginId)) {
                return new View(plugin, workspace);
            }
        }
        return new JButton("Couldn't load");
    }
}
