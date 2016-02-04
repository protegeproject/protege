package org.protege.editor.core.ui.toolbar;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.action.ToolBarActionComparator;
import org.protege.editor.core.ui.action.ToolBarActionPluginJPFImpl;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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
        List<ToolBarActionPluginJPFImpl> list = new ArrayList<>();
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
                LoggerFactory.getLogger(getClass()).warn("Couldn't load main toolbar item", e);
            }
        }
        return toolBar;
    }
}
