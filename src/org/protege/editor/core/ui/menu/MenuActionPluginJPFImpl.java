package org.protege.editor.core.ui.menu;

import org.apache.log4j.Logger;
import org.java.plugin.registry.Extension;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.ExtensionInstantiator;
import org.protege.editor.core.plugin.PluginProperties;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.action.ProtegeActionPluginJPFImpl;

import javax.swing.*;
import java.awt.*;
import java.util.StringTokenizer;
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
 * Date: Mar 27, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An implementation of the <code>MenuActionPlugin</code> which uses
 * the Java Plugin Framework to provide the details of the plugin.
 */
public class MenuActionPluginJPFImpl extends ProtegeActionPluginJPFImpl implements MenuActionPlugin {

    public static final String EXTENSION_POINT_ID = "EditorKitMenuAction";

    private static final String ACCELERATOR_PARAM = "accelerator";

    private static final String PATH_PARAM = "path";

    private static final String SEPARATOR = "/";

    private static final String DYNAMIC_PARAM = "dynamic";

    private String parentId;

    private String group;

    private String groupIndex;


    public MenuActionPluginJPFImpl(EditorKit editorKit, Extension extension) {
        super(editorKit, extension);
        parse();
    }


    private String getPath() {
        // The path corresponds to the path parameter value.
        return PluginProperties.getParameterValue(getExtension(), PATH_PARAM, SEPARATOR);
    }


    public String getParentId() {
        return parentId;
    }


    public String getGroup() {
        return group;
    }


    public String getGroupIndex() {
        return groupIndex;
    }


    public KeyStroke getAccelerator() {
        String acceleratorString = PluginProperties.getParameterValue(getExtension(), ACCELERATOR_PARAM, null);
        if (acceleratorString != null) {
            KeyStroke ks = KeyStroke.getKeyStroke(acceleratorString);
            if (ks != null) {
                return KeyStroke.getKeyStroke(ks.getKeyCode(),
                                              Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | ks.getModifiers());
            }
        }
        return null;
    }


    private KeyStroke parseKeyStroke(String s) {
        StringTokenizer tokenizer = new StringTokenizer(s, " ");
        boolean shift;
        boolean control;
        boolean alt;
        boolean meta;
        char ch = 0;
        boolean found = true;
        while (tokenizer.hasMoreTokens()) {
            String curTok = tokenizer.nextToken();
            if (curTok.equals("shift")) {
                shift = true;
            }
            else if (curTok.equals("control")) {
                control = true;
            }
            else if (curTok.equals("alt")) {
                alt = true;
            }
            else if (curTok.equals("meta")) {
                meta = true;
            }
            else {
                // Key code?
                if (curTok.length() == 1) {
                    found = true;
                    ch = curTok.charAt(0);
                    break;
                }
            }
        }
        if (found) {
            KeyStroke ks = KeyStroke.getKeyStroke(ch);
        }
        return null;
    }


    /**
     * Parses the path to extract the parent id, the
     * group and the group index.  If the group and group
     * index aren't specified then these default to the
     * empty string.
     */
    private void parse() {
        group = "";
        groupIndex = "";
        String path = getPath();
        int separatorIndex = path.indexOf(SEPARATOR);
        if (separatorIndex > -1) {
            parentId = path.substring(0, separatorIndex).trim();
            String groupPart = path.substring(separatorIndex + 1, path.length()).trim();
            int groupPartIndex = groupPart.indexOf("-");
            if (groupPartIndex > -1) {
                group = groupPart.substring(0, groupPartIndex).trim();
                groupIndex = groupPart.substring(groupPartIndex + 1, groupPart.length());
            }
            else {
                group = groupPart;
            }
        }
    }


    /**
     * Creates an instance of the plugin.  It is expected that
     * this instance will be "setup", but the instance's
     * initialise method will not have been called in the instantiation
     * process.
     */
    public ProtegeAction newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ExtensionInstantiator<ProtegeAction> instantiator = new ExtensionInstantiator<ProtegeAction>(getExtension());
        ProtegeAction menuAction = instantiator.instantiate();
        if (menuAction == null) {
            menuAction = new NullMenuAction();
        }
        menuAction.putValue(AbstractAction.NAME, getName());
        String toolTip = getToolTipText();
        if (toolTip != null) {
            toolTip = toolTip.replace("\n", "");
            toolTip = toolTip.replace("\t", "");
            menuAction.putValue(AbstractAction.SHORT_DESCRIPTION, toolTip);
        }
        menuAction.putValue(AbstractAction.ACCELERATOR_KEY, getAccelerator());
        menuAction.setEditorKit(getEditorKit());
        try {
            menuAction.initialise();
        }
        catch (Exception e) {
            Logger.getLogger(MenuActionPluginJPFImpl.class).error(e);
        }
        return menuAction;
    }


    public String getDocumentation() {
        return null;
    }


    /**
     * Determines if the menu is dynamically constructed
     * after the plugin has been loaded.
     */
    public boolean isDynamic() {
        return PluginProperties.getBooleanParameterValue(getExtension(), DYNAMIC_PARAM, false);
    }
}
