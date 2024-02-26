package org.protege.editor.core.ui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.coode.mdock.ComponentNode;
import org.coode.mdock.NodeComponent;
import org.coode.mdock.NodePanel;
import org.coode.mdock.SplitterNode;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.action.ToolBarActionComparator;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 19, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>


 * Represents a view on a model.  A <code>View</code> contains
 * a <code>ViewComponent</code>, which is a type of supported
 * plugin.  The view also contains a header, which consists of a
 * toolbar, which can hold actions for the view and a view banner
 * that contains a label and view manipulation buttons.
 */
public class View extends JComponent implements NodeComponent, Disposable {

    private final Logger logger = LoggerFactory.getLogger(View.class);


    public static final String DETACHED_WINDOWS_FLOAT = "DETACHED_WINDOWS_FLOAT";


	private static final String SPLIT_VERTICALLY_ICON_NAME = "view.splitvertically.gif";

    private static final String SPLIT_HORIZONTALLY_ICON_NAME = "view.horizontalsplit.gif";

    private static final String FLOAT_ICON_NAME = "view.float.gif";

    private static final String CLOSE_ICON_NAME = "view.close.gif";


    // Maintain a reference to the plugin that created
    // the view.
    private ViewComponentPlugin plugin;

    // The workspace tab that the view is a child of.
    private Workspace workspace;

    // The main component holder - this holds the header
    // banner, and toolbar, the view border etc. etc.
    private ViewBarComponent viewBarComponent;

    // A holder for the view component
    private JPanel viewComponentHolder;

    // The "plugin" view component.
    private ViewComponent viewComponent;

    private ComponentNode componentNode;


    private boolean closable = false;

    private boolean floatable = false;

    private boolean splitable = true;

    private boolean pinned = false;

    private boolean syncronizing = true;

    private boolean persist = true;

    // A flag that is used to initialise the view
    // in the most lazy way possible.  The View is
    // only initialised when it is shown
    private boolean initialisedContent = false;

    private Set<ViewActionPlugin> additionalViewActionPlugins;

    // A set to keep track of the view action plugins
    // that we have instantiated.
    private final Set<ViewAction> addedViewActions = new LinkedHashSet<>();


	/**
     * Creates a <code>View</code> that will display the
     * <code>ViewComponent</code> instantiated by the specified
     * tab view plugin.
     * @param plugin    The <code>ViewComponentPlugin</code> that will
     *                  be reponsible for instantiating the content of this <code>View</code>
     *                  in the form of a <code>ViewComponent</code>.
     * @param workspace The parent <code>Workspace</code>.
     */
    public View(ViewComponentPlugin plugin, Workspace workspace) {
        this.plugin = plugin;
        this.workspace = workspace;

        // Use a hierarchy listener so that we know when the view is
        // shown.  When the view is shown, initialised it and remove
        // the hierarchy listener
        addHierarchyListener(new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent e) {
                if (initialisedContent == false && isShowing()) {
                    createUI();
                    initialisedContent = true;
                    removeHierarchyListener(this);
                }
            }
        });
        additionalViewActionPlugins = new HashSet<>();
    }

    public Collection<ViewAction> getViewActions() {
        return new ArrayList<>(addedViewActions);
    }

    public boolean requestFocusInWindow() {
        if (viewComponent == null) {
            return false;
        }
        return viewComponent.requestFocusInWindow();
    }


    public void setShowViewBar(boolean b) {
        viewBarComponent.getViewBar().setVisible(b);
    }


    public void setShowViewBanner(boolean b) {
        viewBarComponent.getViewBar().getViewBanner().setVisible(b);
    }


    public ComponentNode getComponentNode() {
        return componentNode;
    }

    public void addViewMode(final ViewMode viewMode) {
        viewBarComponent.addMode(viewMode);
        char ch = Character.toLowerCase(viewMode.getName().charAt(0));
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(ch), viewMode);
        getActionMap().put(viewMode, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setViewMode(viewMode);
            }
        });
    }

    public Optional<ViewMode> getViewMode() {
        return viewBarComponent.getViewMode();
    }


    public void setViewMode(ViewMode mode) {
        viewBarComponent.setViewMode(mode);
    }

    public void addViewModeChangedHandler(ViewModeChangedHandler handler) {
        viewBarComponent.addViewModeChangedHandler(handler);
    }

    /**
     * Gets the <code>ViewComponentPlugin</code> Id.  This
     * is typically used to configure actions (toolbar buttons)
     * for this view.
     * @return A <code>String</code> representation of the Id
     *         for the <code>ViewComponentPlugin</code>.
     */
    public String getId() {
        return plugin.getId();
    }


    /**
     * Sets the text on the view header.  This can be used to
     * override the label that is specified in the plugin.xml
     * file.
     * @param text The header text.
     */
    public void setHeaderText(String text) {
        viewBarComponent.getViewBar().getViewBanner().setText(text);
    }


    public String getViewName() {
        return plugin.getLabel();
    }


    public void createUI() {
        initialisedContent = true;
        logger.debug("Creating the UI for the '{}' view", this.getViewName());
        setLayout(new BorderLayout(0, 0));
        viewComponentHolder = new JPanel(new BorderLayout());
        viewBarComponent = new ViewBarComponent(getViewName(), plugin.getBackgroundColor(), viewComponentHolder);
        add(viewBarComponent);
        addViewManipulationActions();
        createContent();
        createViewToolBar();
        viewBarComponent.getViewBar().getViewBanner().setPinned(pinned);
    }


    public void setHeaderBackgroundColor(Color color) {
        viewBarComponent.getViewBar().getViewBanner().setBannerColor(color);
    }


    public void hideViewBar() {
        viewBarComponent.getViewBar().setVisible(false);
    }


    private void createViewToolBar() {
        ViewToolBarActionPluginLoader loader = new ViewToolBarActionPluginLoader(workspace.getEditorKit(), this);
        java.util.List<ViewActionPlugin> plugins = new ArrayList<>(loader.getPlugins());
        plugins.addAll(additionalViewActionPlugins);
        Collections.sort(plugins, new ToolBarActionComparator());
        String lastGroup = null;
        for (ViewActionPlugin plugin : plugins) {
            try {
                ViewAction action = (ViewAction) plugin.newInstance();
                if (lastGroup != null) {
                    if (!plugin.getGroup().equals(lastGroup)) {
                        viewBarComponent.getViewBar().addSeparator();
                    }
                }
                if (action.getValue(AbstractAction.SHORT_DESCRIPTION) == null) {
                    action.putValue(AbstractAction.SHORT_DESCRIPTION, action.getValue(AbstractAction.NAME));
                }
                viewBarComponent.addAction(action);
                // Handle accelerators
                KeyStroke acceleratorKeyStroke = (KeyStroke) action.getValue(AbstractAction.ACCELERATOR_KEY);
                if (acceleratorKeyStroke != null) {
                    String key = "Accelerator" + System.currentTimeMillis();
                    getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(acceleratorKeyStroke, key);
                    getActionMap().put(key, action);
                    action.putValue(AbstractAction.SHORT_DESCRIPTION,
                                    action.getValue(AbstractAction.SHORT_DESCRIPTION) + " (" + KeyEvent.getKeyModifiersText(
                                            acceleratorKeyStroke.getModifiers()) + " " + KeyEvent.getKeyText(
                                            acceleratorKeyStroke.getKeyCode()) + ")");
                }
                lastGroup = plugin.getGroup();
                action.setView(this);
                action.setEditorKit(workspace.getEditorKit());
                action.initialise();
                addedViewActions.add(action);
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    protected void addMode(ViewMode viewMode) {
        viewBarComponent.addMode(viewMode);
    }

    protected void addAction(final ProtegeAction action, final String group, final String groupIndex) {
        ViewActionPlugin plugin = new ViewActionPlugin() {
            public String getId() {
                return null;
            }


            public String getName() {
                return (String) action.getValue(AbstractAction.NAME);
            }


            public String getToolTipText() {
                return (String) action.getValue(AbstractAction.SHORT_DESCRIPTION);
            }


            public Icon getIcon() {
                return (Icon) action.getValue(AbstractAction.SMALL_ICON);
            }


            public EditorKit getEditorKit() {
                return workspace.getEditorKit();
            }


            public String getDocumentation() {
                return null;
            }


            public ProtegeAction newInstance() throws ClassNotFoundException, IllegalAccessException,
                                                      InstantiationException {
                return action;
            }


            public View getView() {
                return View.this;
            }


            public String getGroup() {
                return group;
            }


            public String getGroupIndex() {
                return groupIndex;
            }
        };
        additionalViewActionPlugins.add(plugin);
    }


    private void addViewManipulationActions() {
        // View manipulation toolbar
        ViewBanner viewBanner = viewBarComponent.getViewBar().getViewBanner();
        plugin.getHelpLink().ifPresent(u -> viewBanner.addAction("Help", HelpIcon.get(), this::showHelpIfPresent));
        viewBanner.addAction("Split vertically", SplitVerticallyIcon.get(), this::splitVertically);
        viewBanner.addAction("Split horizontally", SplitHorizontallyIcon.get(), this::splitHorizontally);
        viewBanner.addAction("Float", FloatIcon.get(), this::copyAndFloatView);
        viewBanner.addAction("Close", CloseIcon.get(), this::closeView);
    }

    private NodePanel getNodePanel() {
        return (NodePanel) SwingUtilities.getAncestorOfClass(NodePanel.class, this);
    }


    public void splitVerticallyWith(ViewComponentPlugin plugin) {
        if (getNodePanel() != null) {
            ComponentNode newNode = new ComponentNode();
            newNode.add(createView(plugin), plugin.getLabel());
            componentNode.getParent().insertNodeAfter(newNode, componentNode, SplitterNode.VERTICAL_SPLITTER);
            getNodePanel().rebuild();
        }

        else if (getParent() instanceof ViewContainer) {
            ((ViewContainer) getParent()).splitVertically(createView(plugin));
        }
    }


    private View createView(ViewComponentPlugin plugin) {
        View sv = new View(plugin, workspace);
        sv.pinned = true;
        return sv;
    }


    public void splitVertically() {
        splitVerticallyWith(plugin);
    }


    public void splitHorizontally() {
        splitHorizontallyWith(plugin);
    }


    public void splitHorizontallyWith(ViewComponentPlugin plugin) {
        if (getNodePanel() != null) {
            ComponentNode newNode = new ComponentNode();
            newNode.add(createView(plugin), plugin.getLabel());
            componentNode.getParent().insertNodeAfter(newNode, componentNode, SplitterNode.HORIZONTAL_SPLITTER);
            getNodePanel().rebuild();
        }
        else if (getParent() instanceof ViewContainer) {
            ((ViewContainer) getParent()).splitHorizontally(createView(plugin));
        }
    }

    private void showHelpIfPresent() {
        plugin.getHelpLink().ifPresent(l -> {
            try {
                Desktop.getDesktop().browse(l);
            } catch (IOException e1) {
                logger.warn("An error occurred whilst navigating to the help for the {} view.  URL: {}",
                            plugin.getLabel(),
                            l);
            }
        });
    }


    private void createContent() {
        try {
            viewComponent = plugin.newInstance();
            viewComponentHolder.add(viewComponent);
            viewComponent.setView(this);
            viewComponent.initialise();
        }
        catch (Exception e) {
            logger.error("An error occurred whilst creating the view content for the '{}' view: {}", plugin.getLabel(), e);
            viewComponentHolder.add(ComponentFactory.createExceptionComponent(
                    "An error occurred whilst creating the view",
                    e,
                    null));
        }
    }


    /**
     * Determines whether the view is "pinned".  If a view is pinned
     * then it should not synchronise its parent tab's underlying
     * selection model that would normally alter the selection.  This
     * is typically used for view cloning (splitting), where a view is
     * split to compare two different objects - it would not make sense
     * for the cloned view to synchronise its selection with the original
     * view.
     * @return <code>true</code> if the view is pinned and should not be
     *         synchronised with its parent tab's underlying selection model, or
     *         <code>false</code> if the view is not pinned.
     */
    public boolean isPinned() {
        return pinned;
    }


    public void setPinned(boolean pinned) {
        this.pinned = pinned;
        // We could update the header here to reflect the fact that the
        // view is pinned.  May be an icon?
        if (viewBarComponent != null) {
            viewBarComponent.getViewBar().getViewBanner().setPinned(pinned);
        }
    }


    public boolean isClosable() {
        return closable;
    }


    public boolean isFloatable() {
        return floatable;
    }


    public boolean isSplitable() {
        return splitable;
    }


    public boolean isSyncronizing() {
        return syncronizing;
    }


    public boolean persist() {
        return persist;
    }


    public void setSyncronizing(boolean syncronizing) {
        this.syncronizing = syncronizing;
    }


    /**
     * Gets the component that actually displays the view.
     */
    public ViewComponent getViewComponent() {
        return viewComponent;
    }


    public void closeView() {
        // Just remove from parent
        NodePanel nodePanel = (NodePanel) SwingUtilities.getAncestorOfClass(NodePanel.class, this);
        if (nodePanel != null) {
            getParent().remove(this);
            dispose();
            nodePanel.rebuild();
            nodePanel.repaint();
        }
        else {

            if (getParent() instanceof ViewContainer) {
                ((ViewContainer) getParent()).closeView(this);
                dispose();
            }
        }
    }


    private void copyAndFloatView() {
        Dimension size = getSize();
        Point loc = getLocation();
        SwingUtilities.convertPointToScreen(loc, this);
        final View view = createView(plugin);
        final JDialog dlg;
        Preferences appPrefs = PreferencesManager.getInstance().getApplicationPreferences(ProtegeApplication.ID);
        if (appPrefs.getBoolean(DETACHED_WINDOWS_FLOAT, true)) {
        	dlg = new JDialog(ProtegeManager.getInstance().getFrame(workspace));
        }
        else {
        	dlg = new JDialog();
        }
        view.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        viewBarComponent.setEnabled(false);
        JPanel holder = new JPanel(new BorderLayout(3, 3));
        holder.add(view);
        final JCheckBox cb = new JCheckBox();
        JPanel checkBoxHolder = new JPanel(new BorderLayout());
        checkBoxHolder.add(cb, BorderLayout.SOUTH);
        checkBoxHolder.setBorder(BorderFactory.createEmptyBorder(1, 4, 4, 2));
        holder.add(checkBoxHolder, BorderLayout.SOUTH);
        cb.setAction(new AbstractAction("Synchronising") {

            /**
             * 
             */
            private static final long serialVersionUID = -4131922452059512538L;

            public void actionPerformed(ActionEvent e) {
                view.setSyncronizing(cb.isSelected());
                view.setPinned(!cb.isSelected());
            }
        });
        dlg.setContentPane(holder);
        dlg.setSize(size);
        dlg.setLocation(loc);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    view.dispose();
                    view.getViewComponent().dispose();
                }
                catch (Exception e1) {
                    logger.warn("BAD VIEW: (" + view.getViewComponent().getClass().getSimpleName() + ") - exception on dispose: " + e1.getMessage());
                }
            }
        });
        dlg.validate();
        dlg.setVisible(true);
        view.syncronizing = false;
    }


    public void dispose() {
        // Dispose of the view if the content was successfully initialised.
        if (initialisedContent) {
            // Dispose of the actions that were successfully added to the view
            for (ViewAction action : addedViewActions) {
                try {
                    action.dispose();
                }
                catch (Exception e) {
                    logger.warn("BAD ViewAction: (" + action.getClass().getSimpleName() + ") Exception on dispose: " + e.getMessage());
                }
            }
            addedViewActions.clear();
            // Dispose of our view component
            if (viewComponent != null) {
                try {
                    viewComponent.dispose();
                }
                catch (Exception e) {
                    logger.warn("BAD ViewComponent: (" + viewComponent.getClass().getSimpleName() + ") Exception on dispose: " + e.getMessage());
                }
            }
        }
    }


    public void addedToNode(ComponentNode node) {
        this.componentNode = node;
    }

}
