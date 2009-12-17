package org.protege.editor.core.ui.menu;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.platform.OSUtils;
import org.protege.editor.core.plugin.DefaultPluginExtensionMatcher;
import org.protege.editor.core.plugin.PluginExtensionFilter;
import org.protege.editor.core.plugin.PluginParameterExtensionMatcher;
import org.protege.editor.core.plugin.PluginProperties;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.action.ProtegeDynamicAction;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.event.KeyEvent;
import java.util.*;
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
 * The <code>MenuBuilder</code> constructs a <code>JMenuBar</code> based on the
 * installed menu action plugins.
 */
public class MenuBuilder {

    private static final Logger logger = Logger.getLogger(MenuBuilder.class);

    private EditorKit editorKit;

    private Map<MenuActionPlugin, Set<MenuActionPlugin>> parentChildMap;

    private Set<ProtegeAction> actions;


    public MenuBuilder(EditorKit editorKit) {
        this.editorKit = editorKit;
        parentChildMap = new HashMap<MenuActionPlugin, Set<MenuActionPlugin>>();
        actions = new HashSet<ProtegeAction>();
    }


    public JMenuBar buildMenu() {
        JMenuBar menuBar = new JMenuBar();
        parentChildMap.clear();
        Map<String, MenuActionPlugin> idPluginMap = getPlugins();
        for (MenuActionPlugin plugin : idPluginMap.values()) {
            MenuActionPlugin parent = idPluginMap.get(plugin.getParentId());
            getChildren(parent).add(plugin);
        }
        // Should now have a hierarchy of plugins
        List<MenuActionPlugin> topLevelMenus = getSortedList(getChildren(null));
        for (MenuActionPlugin plugin : topLevelMenus) {
            //if (getChildren(plugin).size() > 0) {
            add(plugin, menuBar);
            //}
        }

        return menuBar;
    }


    public Set<ProtegeAction> getActions() {
        return actions;
    }


    private void add(MenuActionPlugin plugin, JComponent component) {
        List<MenuActionPlugin> children = getSortedList(getChildren(plugin));
        if (!children.isEmpty() || plugin.isDynamic()) {
            if (!children.isEmpty()) {
                // Add a JMenu
                JMenu menu = new JMenu(plugin.getName());
                component.add(menu);
                MenuActionPlugin lastPlugin = null;
                for (MenuActionPlugin childPlugin : children) {
                    if (lastPlugin != null) {
                        if (!lastPlugin.getGroup().equals(childPlugin.getGroup())) {
                            menu.addSeparator();
                        }
                    }
                    add(childPlugin, menu);
                    lastPlugin = childPlugin;
                }
            }
            else if (plugin.isDynamic()) {
                // Construct dynamic menu.  This is basically
                // a menu, whose children are determined at runtime.
                final JMenu menu = new JMenu(plugin.getName());
                menu.add(new JMenuItem());
                component.add(menu);
                try {
                    // The menu must be a dynamic action menu.
                    final ProtegeDynamicAction action = (ProtegeDynamicAction) plugin.newInstance();
                    menu.addMenuListener(new MenuListener() {
                        public void menuSelected(MenuEvent e) {
                            // Rebuild the menu
                            menu.removeAll();
                            action.rebuildChildMenuItems(menu);
                        }


                        public void menuDeselected(MenuEvent e) {
                        }


                        public void menuCanceled(MenuEvent e) {

                        }
                    });
                }
                catch (Exception e) {
                    logger.error(e);
                }
            }
        }
        else {
            // Add a menu item
            if (plugin.getParentId().length() > 0) {
                try {
                    ProtegeAction action = plugin.newInstance();
                    final JMenuItem menuItem = new JMenuItem(action);
                    KeyStroke ks = plugin.getAccelerator();
                    // A nasty hack here.  Mac users have complained that the standard
                    // delete key is backspace (in the finder, CMD+Backspace deletes file).
                    // Since there is generally no delete key on a Mac keyboard, I have
                    // decided to swap this here (MH).
                    if (ks != null) {
                        if (ks.getKeyCode() == KeyEvent.VK_DELETE) {
                            if (OSUtils.isOSX()) {
                                ks = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, ks.getModifiers());
                            }
                        }
                    }
                    menuItem.setAccelerator(ks);
                    component.add(menuItem);
                    actions.add(action);
                }
                catch (Exception e) {
                    logger.error(e);
                }
            }
            else {
                // This is a top level menu.  It is probably a place
                // holder, so add it.
                JMenu menu = new JMenu(plugin.getName());
                component.add(menu);
            }
        }
    }


    private Map<String, MenuActionPlugin> getPlugins() {
        // Create a map to hold the results in.  This maps menu plugin ids to their
        // menus so that we can form an ordering of parent child menu plugins
        Map<String, MenuActionPlugin> result = new HashMap<String, MenuActionPlugin>();
        // Load general items that are available for any clsdescriptioneditor kit
        PluginExtensionFilter generalFilter = new PluginExtensionFilter(ProtegeApplication.ID,
                                                                        MenuActionPluginJPFImpl.EXTENSION_POINT_ID,
                                                                        new DefaultPluginExtensionMatcher());

        PluginParameterExtensionMatcher generalMatcher = new PluginParameterExtensionMatcher();
        generalMatcher.put(PluginProperties.EDITOR_KIT_PARAM_NAME, "any");
        for (IExtension ext : generalFilter.getExtensions()) {
            MenuActionPluginJPFImpl plugin = new MenuActionPluginJPFImpl(editorKit, ext);
            result.put(plugin.getId(), plugin);
        }
        // Load items that are specific to the current clsdescriptioneditor kit
        PluginParameterExtensionMatcher specificMatcher = new PluginParameterExtensionMatcher();
        specificMatcher.put(PluginProperties.EDITOR_KIT_PARAM_NAME, editorKit.getId());
        PluginExtensionFilter specificFilter = new PluginExtensionFilter(ProtegeApplication.ID,
                                                                         MenuActionPluginJPFImpl.EXTENSION_POINT_ID,
                                                                         specificMatcher);
        for (IExtension ext : specificFilter.getExtensions()) {
            MenuActionPluginJPFImpl plugin = new MenuActionPluginJPFImpl(editorKit, ext);
            result.put(ext.getUniqueIdentifier(), plugin);
        }
        // Dynamically create plugins for the installed clsdescriptioneditor kits.  These are menu items
        // that are on the File -> Open, and File -> New menus
//        for (EditorKitFactoryPlugin edKitFactoryPlugin : ProtegeManager.getInstance().getEditorKitFactoryPlugins()) {
//            EditorKitFactoryOpenActionPlugin openActionPlugin = new EditorKitFactoryOpenActionPlugin(
//                    edKitFactoryPlugin);
//            result.put(openActionPlugin.getId(), openActionPlugin);
//            EditorKitFactoryNewActionPlugin newActionPlugin = new EditorKitFactoryNewActionPlugin(edKitFactoryPlugin);
//            result.put(newActionPlugin.getId(), newActionPlugin);
//        }
        return result;
    }


    private Set<MenuActionPlugin> getChildren(MenuActionPlugin plugin) {
        Set<MenuActionPlugin> children = parentChildMap.get(plugin);
        if (children == null) {
            children = new HashSet<MenuActionPlugin>();
            parentChildMap.put(plugin, children);
        }
        return children;
    }


    private static List<MenuActionPlugin> getSortedList(Set<MenuActionPlugin> plugins) {
        List<MenuActionPlugin> list = new ArrayList<MenuActionPlugin>(plugins);
        Collections.sort(list, new MenuActionPluginComparator());
        return list;
    }


    private static class MenuActionPluginComparator implements Comparator<MenuActionPlugin> {

        /**
         * Compares two <code>MenuActionPlugin</code>s based on their
         * groups.  Groups are compared alphabetically.  If their groups are
         * equal then the group indexes are compared, which are also compared
         * alphabetically.
         */
        public int compare(MenuActionPlugin o1, MenuActionPlugin o2) {
            int result = o1.getGroup().compareTo(o2.getGroup());
            if (result == 0) {
                result = o1.getGroupIndex().compareTo(o2.getGroupIndex());
            }
            return result;
        }
    }
}
