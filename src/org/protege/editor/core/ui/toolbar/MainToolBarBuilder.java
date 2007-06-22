package org.protege.editor.core.ui.toolbar;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.action.ToolBarActionComparator;
import org.protege.editor.core.ui.action.ToolBarActionPluginJPFImpl;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * Date: Mar 28, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MainToolBarBuilder {

    private EditorKit editorKit;


    public MainToolBarBuilder(EditorKit editorKit) {
        this.editorKit = editorKit;
    }


    public JToolBar getToolBar() {
        JToolBar toolBar = new JToolBar();
        // Add actions to the toolbar
        MainToolBarActionPluginLoader editorKitSpecificLoader = new MainToolBarActionPluginLoader(editorKit);
        List<ToolBarActionPluginJPFImpl> list = new ArrayList<ToolBarActionPluginJPFImpl>();
        list.addAll(editorKitSpecificLoader.getPlugins());
        // Add general actions that apply to any clsdescriptioneditor kit.  This
        // mean passing in a null for the clsdescriptioneditor kit
        MainToolBarActionPluginLoader generalLoader = new MainToolBarActionPluginLoader(null);
        list.addAll(generalLoader.getPlugins());
        // Now sort the list of items and add them to to the toolbar
        Collections.sort(list, new ToolBarActionComparator());
        ToolBarActionPluginJPFImpl lastPlugin = null;
        for (ToolBarActionPluginJPFImpl plugin : list) {
            try {
                if (lastPlugin != null) {
                    if (lastPlugin.getGroup().equals(plugin.getGroup()) == false) {
                        toolBar.addSeparator();
                    }
                }
                ProtegeAction action = plugin.newInstance();
                toolBar.add(action);
                action.initialise();
            }
            catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Couldn't load main toolbar item", e);
            }
        }
        return toolBar;
    }
}
