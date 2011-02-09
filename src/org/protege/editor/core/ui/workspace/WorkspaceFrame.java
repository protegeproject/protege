package org.protege.editor.core.ui.workspace;

import org.protege.editor.core.platform.OSUtils;
import org.protege.editor.core.platform.apple.ProtegeAppleApplication;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.menu.MenuBuilder;
import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginLoader;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 15, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * The <code>WorkspaceFrame</code> is a holder for a <code>Workspace</code>.
 * The frame menu bar and toolbar are constructed for the particular workspace.
 */
public class WorkspaceFrame extends JFrame {

    private static final String VIEW = "Views";

	/**
     * 
     */
    private static final long serialVersionUID = -8568184212386766789L;

    private Workspace workspace;

    public static final String LOC_X = "LOC_X";

    public static final String LOC_Y = "LOC_Y";

    public static final String SIZE_X = "SIZE_X";

    public static final String SIZE_Y = "SIZE_Y";

    private Set<ProtegeAction> menuActions;


    public WorkspaceFrame(Workspace workspace) {
        this.workspace = workspace;
        menuActions = new HashSet<ProtegeAction>();
        createUI();
        restoreMetrics();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (OSUtils.isOSX()){
                    ProtegeAppleApplication.getInstance().setEditorKit(null);
                }
                saveMetrics();
                removeWindowListener(this);
            }

            public void windowActivated(WindowEvent event) {
                if (OSUtils.isOSX()){
                    ProtegeAppleApplication.getInstance().setEditorKit(WorkspaceFrame.this.workspace.getEditorKit());
                }
            }
        });
    }


    public void dispose() {
        super.dispose();

        for (ProtegeAction action : menuActions) {
            try {
                action.dispose();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    protected void restoreMetrics() {
        Preferences p = PreferencesManager.getInstance().getApplicationPreferences(getClass().getName());
        int w = p.getInt(SIZE_X, 800);
        int h = p.getInt(SIZE_Y, 600);
        setSize(w, h);
        Point defLoc = getDefaultLocation();
        int x = p.getInt(LOC_X, defLoc.x);
        int y = p.getInt(LOC_Y, defLoc.y);
        setLocation(x, y);
        setSize(w, h);
    }


    private Point getDefaultLocation() {
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameDim = getSize();
        return new Point((screenDim.width - frameDim.width) / 2, (screenDim.height - frameDim.height) / 2);
    }


    protected void saveMetrics() {
        Preferences p = PreferencesManager.getInstance().getApplicationPreferences(getClass().getName());
        p.putInt(LOC_X, getLocation().x);
        p.putInt(LOC_Y, getLocation().y);
        p.putInt(SIZE_X, getSize().width);
        p.putInt(SIZE_Y, getSize().height);
    }


    public JMenu getMenu(String name) {
        for (int i = 0; i < getJMenuBar().getMenuCount(); i++) {
            JMenu menu = getJMenuBar().getMenu(i);
            if (menu.getText() != null) {
                if (menu.getText().equals(name)) {
                    return menu;
                }
            }
        }
        JMenu menu = new JMenu(name);
        getJMenuBar().add(menu);
        return menu;
    }


    private void createUI() {
        JPanel contentPane = new JPanel(new BorderLayout(7, 7));
        setContentPane(contentPane);
        // Menu bar
        createMenuBar();

        // Add the workspace to the frame
        contentPane.add(workspace);
        workspace.initialiseExtraMenuItems(getJMenuBar());
        String title = workspace.getTitle();
        if (title != null) {
            setTitle(title);
        }
        setIconImage(((ImageIcon) Icons.getIcon("logo32.gif")).getImage());

        JComponent statusArea = workspace.getStatusArea();
        if (statusArea != null) {
            contentPane.add(statusArea, BorderLayout.SOUTH);
        }
    }


    public void updateTitle() {
        String title = workspace.getTitle();
        if (title != null) {
            setTitle(title);
        }
    }


    private void createMenuBar() {
        // Delegate to the menu builder, which will create the
        // menus based on installed plugins.
        MenuBuilder menuBuilder = new MenuBuilder(workspace.getEditorKit());
        setJMenuBar(menuBuilder.buildMenu());
        // Views menu - this is a special menu
        final JMenu viewMenu = getMenu(VIEW);
        viewMenu.addMenuListener(new MenuListener() {

            public void menuSelected(MenuEvent e) {
                buildViewMenu();
            }


            public void menuDeselected(MenuEvent e) {
                viewMenu.removeAll();
            }


            public void menuCanceled(MenuEvent e) {
                viewMenu.removeAll();
            }
        });
        menuActions.addAll(menuBuilder.getActions());
    }


    private void buildViewMenu() {
        if (workspace instanceof TabbedWorkspace == false) {
            // Don't bother to show a view menu for non
            // tabbed workspaces.
            return;
        }

        // First categorise them

        Map<String, List<ViewComponentPlugin>> categoriesMap = new HashMap<String, List<ViewComponentPlugin>>();

        ViewComponentPluginLoader loader = new ViewComponentPluginLoader(workspace);
        for (ViewComponentPlugin plugin : loader.getPlugins()) {
            Set<String> categories = plugin.getCategorisations();
            if (!categories.isEmpty()) {
                for (String category : categories) {
                    List<ViewComponentPlugin> plugins = categoriesMap.get(category);
                    if (plugins == null) {
                        plugins = new ArrayList<ViewComponentPlugin>();
                        categoriesMap.put(category, plugins);
                    }
                    plugins.add(plugin);
                }
            }
            else {
                List<ViewComponentPlugin> plugins = categoriesMap.get("Misc");
                if (plugins == null) {
                    plugins = new ArrayList<ViewComponentPlugin>();
                    categoriesMap.put("Misc", plugins);
                }
                plugins.add(plugin);
            }
        }

        // Create a sub menu for each category
        final JMenu viewMenu = getMenu(VIEW);

        List<String> categories = new ArrayList<String>();
        categories.addAll(categoriesMap.keySet());
        Collections.sort(categories);
        for (String category : categories) {
            JMenu subMenu = new JMenu(category + " views");
            viewMenu.add(subMenu);
            List<ViewComponentPlugin> viewPlugins = new ArrayList<ViewComponentPlugin>(categoriesMap.get(category));
            // Sort them
            Collections.sort(viewPlugins, new Comparator<ViewComponentPlugin>() {
                public int compare(ViewComponentPlugin o1, ViewComponentPlugin o2) {
                    return o1.getLabel().compareTo(o2.getLabel());
                }
            });

            for (final ViewComponentPlugin plugin : viewPlugins) {
                Action action = new AbstractAction(plugin.getLabel()) {
                    /**
                     * 
                     */
                    private static final long serialVersionUID = 282453625948165209L;

                    public void actionPerformed(ActionEvent e) {
                        showView(plugin);
                    }
                };
                action.putValue(AbstractAction.SHORT_DESCRIPTION, plugin.getDocumentation());
                subMenu.add(action);
            }
        }
    }


    private void showView(ViewComponentPlugin plugin) {
        WorkspaceViewManager viewManager = workspace.getViewManager();
        viewManager.showView(plugin.getId());
    }


}
