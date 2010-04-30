package org.protege.editor.core.ui.menu;

import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.log4j.Logger;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.platform.OSUtils;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.action.ProtegeDynamicAction;


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
            if (logger.isDebugEnabled()) {
                logger.debug("" + parent + " parent of " + plugin);
            }
        }
        // Should now have a hierarchy of plugins
        List<MenuActionPlugin> topLevelMenus = getSortedList(getChildren(null));
        for (MenuActionPlugin plugin : topLevelMenus) {
            add(plugin, menuBar);
        }

        return menuBar;
    }


    public Set<ProtegeAction> getActions() {
        return actions;
    }


    private void add(MenuActionPlugin plugin, JComponent component) {
        if (logger.isDebugEnabled()) {
            if (component instanceof JMenuBar) {
                logger.debug("Adding " + plugin + " to menu bar");
            }
            else if (component instanceof JMenu) {
                logger.debug("Giving " + ((JMenu) component).getText() + " the child" + plugin);
            }
            else {
                logger.debug("Modify this log message, please");
            }
        }
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
                    final JMenuItem menuItem = plugin.isJCheckBox() ? new JCheckBoxMenuItem(action) : new JMenuItem(action);
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
                    try {
                        Method m = action.getClass().getMethod("setMenuItem", JMenuItem.class);
                        m.invoke(action, menuItem);
                    }
                    catch (Throwable t) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Action " + action + " is not requesting a menu item" + t);
                        }
                    }
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
        MenuActionPluginLoader pluginLoader = new MenuActionPluginLoader(editorKit);
        for (MenuActionPlugin plugin : pluginLoader.getPlugins()) {
            result.put(plugin.getId(), plugin);
        }
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
