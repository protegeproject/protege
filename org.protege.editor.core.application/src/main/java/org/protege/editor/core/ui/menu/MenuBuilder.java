package org.protege.editor.core.ui.menu;

import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.util.*;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
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
	private Set<ProtegeAction> actions;

	private Multimap<MenuActionPlugin, MenuActionPlugin> parentChildMap;
	private Map<String, ButtonGroup> group2ButtonGroupMap;


	public MenuBuilder(EditorKit editorKit) {
		this.editorKit = editorKit;
		parentChildMap = HashMultimap.create();
		group2ButtonGroupMap = new HashMap<>();
		actions = new HashSet<>();
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
			addMenu(plugin, menuBar);
		}

		return menuBar;
	}

	private void addMenu(MenuActionPlugin plugin, JComponent menuContainer) {
		logCreateMenu(plugin, menuContainer);
		List<MenuActionPlugin> children = getSortedList(getChildren(plugin));
		
		if (!children.isEmpty()) { //this is a recursive function, it is important to have a stop condition
			buildCompositeMenu(plugin, menuContainer, children);
		} else {
			if (plugin.isDynamic()) {
				buildDynamicMenu(plugin, menuContainer);
			} else {
				if (hasParentMenu(plugin)) {
					buildInnerMenu(plugin, menuContainer);	
				} else {
					buildTopLevelMenu(plugin, menuContainer);
				}				
			}
		}
	}


	private void buildCompositeMenu(MenuActionPlugin plugin, JComponent menuContainer, List<MenuActionPlugin> children) {
		// Add a JMenu
		JMenu menu = new JMenu(plugin.getName());
		menuContainer.add(menu);
		MenuActionPlugin lastPlugin = null;
		for (MenuActionPlugin childPlugin : children) {
			if (lastPlugin != null) {
				if (!lastPlugin.getGroup().equals(childPlugin.getGroup())) {
					menu.addSeparator();
				}
			}
			addMenu(childPlugin, menu); //recursive call
			lastPlugin = childPlugin;
		}	    
	}

	private void buildDynamicMenu(MenuActionPlugin plugin, JComponent menuContainer) {
		// Construct dynamic menu.  This is basically a menu, whose children are determined at runtime.
		final JMenu menu = new JMenu(plugin.getName());
		menuContainer.add(menu);
		try {
			// The menu must be a dynamic action menu.
			final ProtegeDynamicAction action = (ProtegeDynamicAction) plugin.newInstance();                    

			invokeDynamicMenuMethods(action, menu);

			menu.addMenuListener(getDynamicMenuListener(menu, action));
			actions.add(action);
		}
		catch (Exception e) {
			logger.error(e);
		}
	}
	
	private void buildInnerMenu(MenuActionPlugin plugin, JComponent menuContainer) {
		try {
			ProtegeAction action = plugin.newInstance();
			final JMenuItem menuItem = createMenuItem(plugin, action);                    
			KeyStroke ks = plugin.getAccelerator();
			ks = fixAcceleratorForMacOSX(ks);
			menuItem.setAccelerator(ks);
			menuContainer.add(menuItem);
			
			invokeInnerMenuMethods(action, menuItem, menuContainer);

			actions.add(action);
		}
		catch (Exception e) {
			ProtegeApplication.getErrorLog().logError(e);
		}
		catch (NoClassDefFoundError noClass) {
			logger.error("Error loading menu plugin " + plugin.getId() + "(" + plugin.getName() + ")");
			ProtegeApplication.getErrorLog().logError(noClass);
		}
	}
	
	private void buildTopLevelMenu(MenuActionPlugin plugin, JComponent menuContainer) {
		// This is a top level menu.  It is probably a place holder, so add it.
		JMenu menu = new JMenu(plugin.getName());
		menuContainer.add(menu);	    
	}


	private JMenuItem createMenuItem(MenuActionPlugin plugin, ProtegeAction action) {
		if (plugin.isCheckBox()) {
			return new JCheckBoxMenuItem(action);
		} else if (plugin.isRadioButton()) {
			JRadioButtonMenuItem radioButtonMenuItem = new JRadioButtonMenuItem(action);
			ButtonGroup group = getButtonGroup(plugin);
			if (group != null) {
				group.add(radioButtonMenuItem);
			}
			return radioButtonMenuItem;
		}
		return new JMenuItem(action);
	}


	private ButtonGroup getButtonGroup(MenuActionPlugin plugin) {
		String group = plugin.getGroup();
		if (group == null) {
			return null;
		}
		ButtonGroup buttonGroup = group2ButtonGroupMap.get(group);
		if (buttonGroup == null) {
			buttonGroup = new ButtonGroup();
			group2ButtonGroupMap.put(group, buttonGroup);
		}
		return buttonGroup;
	}


	private KeyStroke fixAcceleratorForMacOSX(KeyStroke ks) {
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
		return ks;
	}


	private MenuListener getDynamicMenuListener(final JMenu menu, final ProtegeDynamicAction action) {
		return new MenuListener() {
			public void menuSelected(MenuEvent e) {
				menu.removeAll();
				action.rebuildChildMenuItems(menu);
			}
	
			public void menuDeselected(MenuEvent e) {}
			public void menuCanceled(MenuEvent e) {}
		};
	}


	private void invokeDynamicMenuMethods(ProtegeDynamicAction action, JMenu menu) {
		//TT: This can be avoided by adding the method in the interface. Is it fine to change the interface?
	    //TR: It would be nice to change the interface but plugins would break.
		try {
			Method m = action.getClass().getMethod("setMenu", JMenu.class);
			m.invoke(action, menu);
		}
		catch (Throwable t) {
			if (logger.isDebugEnabled()) {
				logger.debug("Action " + action + " is not requesting a menu item" + t);
			}
		}
	}


	private void invokeInnerMenuMethods(ProtegeAction action, JMenuItem menuItem, JComponent menuContainer) {
		try {
			Method m = action.getClass().getMethod("setMenuItem", JMenuItem.class);
			m.invoke(action, menuItem);
		}
		catch (Throwable t) {
			if (logger.isDebugEnabled()) {
				logger.debug("Action " + action + " is not requesting a menu item" + t);
			}
		}
		try {
			Method m = action.getClass().getMethod("setMenuParent", JComponent.class);
			m.invoke(action, menuContainer);
		}
		catch (Throwable t) {
			if (logger.isDebugEnabled()) {
				logger.debug("Action " + action + " is not requesting its parent" + t);
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


	private Collection<MenuActionPlugin> getChildren(MenuActionPlugin plugin) {
		return parentChildMap.get(plugin);
	}


	private static List<MenuActionPlugin> getSortedList(Collection<MenuActionPlugin> plugins) {
		List<MenuActionPlugin> list = new ArrayList<MenuActionPlugin>(plugins);
		Collections.sort(list, new MenuActionPluginComparator());
		return list;
	}


	private boolean hasParentMenu(MenuActionPlugin plugin) {
		return plugin.getParentId().length() > 0;
	}

	public Set<ProtegeAction> getActions() {
		return actions;
	}

	private void logCreateMenu(MenuActionPlugin plugin, JComponent menuContainer) {
		if (logger.isDebugEnabled()) {
			if (menuContainer instanceof JMenuBar) {
				logger.debug("Adding " + plugin + " to menu bar");
			}
			else if (menuContainer instanceof JMenu) {
				logger.debug("Giving " + ((JMenu) menuContainer).getText() + " the child " + plugin);
			}
			else {
				logger.debug("Modify this log message, please");
			}
		}
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
