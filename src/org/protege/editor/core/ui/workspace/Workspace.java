package org.protege.editor.core.ui.workspace;

import com.jgoodies.looks.FontPolicies;
import com.jgoodies.looks.FontPolicy;
import com.jgoodies.looks.FontSet;
import com.jgoodies.looks.FontSets;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import org.apache.log4j.Logger;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ProtegeProperties;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.platform.OSUtils;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.about.AboutPanel;
import org.protege.editor.core.ui.preferences.PreferencesDialogPanel;
import org.protege.editor.core.ui.split.ViewSplitPane;
import org.protege.editor.core.ui.util.ProtegePlasticTheme;
import org.protege.editor.core.ui.view.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Set;

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
 * Date: Mar 15, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A <code>Workspace</code> is a UI component that
 * presents a view on an <code>EditorKit</code>.
 */
public abstract class Workspace extends JComponent implements Disposable {

    /**
     *
     */
    private static final long serialVersionUID = 1737700990946291204L;

    public static final int BOTTOM_RESULTS_VIEW = 0;

    public static final int LEFT_RESULTS_VIEW = 1;

    public static final Logger LOGGER = Logger.getLogger(Workspace.class);

    public static final String FILE_MENU_NAME = "File";

    public static final String WINDOW_MENU_NAME = "Window";

    private static final String HELP_MENU_NAME = "Help";

    public static final String RESULT_PANE_ID = "org.protege.editor.core.resultspane";

    private EditorKit editorKit;

    private WorkspaceViewManager viewManager;

    /**
     * A split pane that holds the tabs and results view
     */
    private ViewSplitPane bottomResultsSplitPane;

    private ViewSplitPane leftResultsSplitPane;

    private ViewHolder bottomResultsViewHolder;

    private ViewHolder leftResultsViewHolder;

    private int fontSize = 12;


    /**
     * This method is called by the system to
     * set up the <code>Workspace</code> (with references
     * to the <code>EditorKit</code> etc.)
     * @param editorKit The <code>EditorKit</code> that this
     * <code>Workspace</code> belongs to.
     */
    public void setup(EditorKit editorKit) {
        this.editorKit = editorKit;
        this.viewManager = new WorkspaceViewManager();
        // Create the layout.
        setLayout(new BorderLayout());

        leftResultsSplitPane = new ViewSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        leftResultsSplitPane.setResizeWeight(0.2);
        add(leftResultsSplitPane);

        bottomResultsSplitPane = new ViewSplitPane(JSplitPane.VERTICAL_SPLIT);
        bottomResultsSplitPane.setResizeWeight(0.65);
        leftResultsSplitPane.add(bottomResultsSplitPane, JSplitPane.RIGHT);

        bottomResultsViewHolder = new ViewHolder("Results", JSplitPane.BOTTOM, bottomResultsSplitPane);
        adjustBorder(bottomResultsViewHolder);
        leftResultsViewHolder = new ViewHolder("R", JSplitPane.LEFT, leftResultsSplitPane);
        adjustBorder(leftResultsViewHolder);
    }


    public int getFontSize() {
        return fontSize;
    }


    public void changeFontSize(int delta) {
        fontSize += delta;
        if (fontSize < 1) {
            fontSize = 1;
        }
    }


    private static void adjustBorder(ViewHolder holder) {
        Border currentBorder = holder.getBorder();
        holder.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7), currentBorder));
    }


    protected void initialiseExtraMenuItems(JMenuBar menuBar) {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu.getText().equals(WINDOW_MENU_NAME)) {
                installLookAndFeelMenu(menu);
            }
            else if (menu.getText().equals(FILE_MENU_NAME)) {
                if (!OSUtils.isOSX()) {
                    final JMenuItem menuItem = new JMenuItem(new AbstractAction("Preferences...") {
                        /**
                         *
                         */
                        private static final long serialVersionUID = -4897769796985728041L;

                        public void actionPerformed(ActionEvent event) {
                            PreferencesDialogPanel.showPreferencesDialog(null, getEditorKit());
                        }
                    });
                    KeyStroke ks = KeyStroke.getKeyStroke(",");
                    menuItem.setAccelerator(ks);
                    menu.addSeparator();
                    menu.add(menuItem);
                    menu.addSeparator();
                    menu.add(new AbstractAction("Exit") {
                        /**
                         *
                         */
                        private static final long serialVersionUID = -3497054762240815779L;

                        public void actionPerformed(ActionEvent event) {
                            ProtegeApplication.handleQuit();
                        }
                    });
                }
            }
            else if (menu.getText().equals(HELP_MENU_NAME)) {
                if (!OSUtils.isOSX()) {
                    menu.addSeparator();
                    menu.add(new AbstractAction("About") {
                        /**
                         *
                         */
                        private static final long serialVersionUID = 3773470646910947172L;

                        public void actionPerformed(ActionEvent event) {
                            AboutPanel.showDialog();
                        }
                    });
                }
            }
        }
    }


    private void installLookAndFeelMenu(JMenu windowMenu) {
        windowMenu.addSeparator();
        JMenu menu = new JMenu("Look & Feel");
        ButtonGroup lafMenuItemGroup = new ButtonGroup();

        windowMenu.add(menu);

        Preferences p = PreferencesManager.getInstance().getApplicationPreferences(ProtegeApplication.LOOK_AND_FEEL_KEY);
        String lafName = p.getString(ProtegeApplication.LOOK_AND_FEEL_CLASS_NAME, "");

        JRadioButtonMenuItem protegeDefaultMenuItem = new JRadioButtonMenuItem(new AbstractAction("Protege Default") {
            /**
             *
             */
            private static final long serialVersionUID = -1460075802676626382L;

            public void actionPerformed(ActionEvent arg0) {
                setProtegeDefaultLookAndFeel(ProtegeProperties.PLASTIC_LAF_NAME);
            }
        });
        lafMenuItemGroup.add(protegeDefaultMenuItem);
        protegeDefaultMenuItem.setSelected(lafName.equals(ProtegeProperties.PLASTIC_LAF_NAME));
        menu.add(protegeDefaultMenuItem);

        for (final UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            final String className = info.getClassName();
            JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(new AbstractAction(info.getName()) {
                /**
                 *
                 */
                private static final long serialVersionUID = 2912631603213508312L;

                public void actionPerformed(ActionEvent e) {
                    setLookAndFeel(className);
                }
            });
            lafMenuItemGroup.add(menuItem);
            menuItem.setSelected(lafName.equals(className));
            menu.add(menuItem);
        }

        JRadioButtonMenuItem plastic3DmenuItem = new JRadioButtonMenuItem(new AbstractAction("Plastic 3D") {
            /**
             *
             */
            private static final long serialVersionUID = 6933202663872017353L;

            public void actionPerformed(ActionEvent e) {
                setLookAndFeel(ProtegeProperties.PLASTIC_3D_LAF);
            }
        });
        lafMenuItemGroup.add(plastic3DmenuItem);
        plastic3DmenuItem.setSelected(lafName.equals(ProtegeProperties.PLASTIC_3D_LAF));
        menu.add(plastic3DmenuItem);
    }


    private void setProtegeDefaultLookAndFeel(String lafName) {
        try {
            LookAndFeel lookAndFeel = (LookAndFeel) Class.forName(lafName).newInstance();

            PopupFactory.setSharedInstance(new PopupFactory());
            PlasticLookAndFeel.setCurrentTheme(new ProtegePlasticTheme());
            PlasticLookAndFeel.setTabStyle(PlasticLookAndFeel.TAB_STYLE_METAL_VALUE);

            FontSet fontSet = FontSets.createDefaultFontSet(ProtegePlasticTheme.DEFAULT_FONT);
            FontPolicy fixedPolicy = FontPolicies.createFixedPolicy(fontSet);
            PlasticLookAndFeel.setFontPolicy(fixedPolicy);

            UIManager.put("ClassLoader", lookAndFeel.getClass().getClassLoader());
            UIManager.setLookAndFeel(lookAndFeel);

//copied from below
            SwingUtilities.updateComponentTreeUI(Workspace.this);

            Preferences p = PreferencesManager.getInstance().getApplicationPreferences(ProtegeApplication.LOOK_AND_FEEL_KEY);
            p.putString(ProtegeApplication.LOOK_AND_FEEL_CLASS_NAME, lafName);

        }
        catch (ClassNotFoundException e) {
            LOGGER.warn("Look and feel not found: " + lafName);
        }
        catch (Exception e) {
            LOGGER.warn(e.toString());
        }
    }


    private void setLookAndFeel(String clsName) {
        try {
            UIManager.setLookAndFeel(clsName);
            SwingUtilities.updateComponentTreeUI(Workspace.this);
            Preferences p = PreferencesManager.getInstance().getApplicationPreferences(ProtegeApplication.LOOK_AND_FEEL_KEY);
            p.putString(ProtegeApplication.LOOK_AND_FEEL_CLASS_NAME, clsName);
        }
        catch (Exception e1) {
            LOGGER.error("Exception caught setting look and feel ", e1);
        }
    }


    protected void setContent(JComponent component) {
        bottomResultsSplitPane.setTopComponent(component);
    }


    /**
     * Gets the <code>EditorKit</code> that this <code>Workspace</code>
     * belongs to.
     */
    public EditorKit getEditorKit() {
        return editorKit;
    }


    public WorkspaceViewManager getViewManager() {
        return viewManager;
    }

    public abstract JComponent getStatusArea();


    public void showResultsView(String id, String headerLabel, Color headerColor, ViewComponent viewComponent, boolean replace, int location) {
        ViewComponentPlugin plugin = new ResultsViewComponentPlugin(id, headerLabel, headerColor, viewComponent);
        showResultsView(plugin, replace, location);
    }


    public View showResultsView(String id, boolean replace, int location) {
        ViewComponentPluginLoader pluginLoader = new ViewComponentPluginLoader(this);
        for (ViewComponentPlugin plugin : pluginLoader.getPlugins()) {
            if (id.equals(plugin.getId())) {
                return showResultsView(plugin, replace, location);
            }
        }
        return null;
    }


    public View showResultsView(ViewComponentPlugin plugin, boolean replace, int location) {
        try {
            ViewHolder viewHolder;
            if (location == BOTTOM_RESULTS_VIEW) {
                viewHolder = bottomResultsViewHolder;
            }
            else {
                viewHolder = leftResultsViewHolder;
            }

            if (replace) {
                View v = viewHolder.getView(plugin.getId());
                if (v != null) {
                    v.closeView();
                }
            }
            View view = new View(plugin, this);
            ViewComponent viewComponent = plugin.newInstance();
            viewComponent.setup(plugin);
            viewHolder.addView(view);
            return view;
        }
        catch (ClassNotFoundException e) {
            ProtegeApplication.getErrorLog().logError(e);
        }
        catch (IllegalAccessException e) {
            ProtegeApplication.getErrorLog().logError(e);
        }
        catch (InstantiationException e) {
            ProtegeApplication.getErrorLog().logError(e);
        }
        return null;
    }


    public void save() throws Exception {
        // By default, do nothing 
    }


    public void dispose() {
        leftResultsViewHolder.dispose();
        bottomResultsViewHolder.dispose();
    }


    protected String getTitle() {
        return "";
    }

//////////////////////////////////////////////////////////////////////////////////
//


    private class ResultsViewComponentPlugin implements ViewComponentPlugin {

        private String id;

        private ViewComponent viewComponent;

        private String headerLabel;

        private Color headerColor;


        protected ResultsViewComponentPlugin(String id, String headerLabel, Color headerColor, ViewComponent viewComponent) {
            this.id = id;
            this.headerColor = headerColor;
            this.headerLabel = headerLabel;
            this.viewComponent = viewComponent;
        }


        public Color getBackgroundColor() {
            return headerColor;
        }


        public String getLabel() {
            return headerLabel;
        }


        public Workspace getWorkspace() {
            return Workspace.this;
        }


        public boolean isUserCreatable() {
            return false;
        }


        public String getDocumentation() {
            return null;
        }


        public String getId() {
            return id;
        }


        public ViewComponent newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
            return viewComponent;
        }


        public Set<String> getCategorisations() {
            return Collections.singleton("Results");
        }


        public Set<String> getNavigates() {
            return Collections.EMPTY_SET;
        }
    }
}
