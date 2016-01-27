package org.protege.editor.owl.model;

import com.google.common.base.Optional;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.ui.RefreshableComponent;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.action.ProtegeActionPluginJPFImpl;
import org.protege.editor.core.ui.error.ErrorLog;
import org.protege.editor.core.ui.error.ErrorLogListener;
import org.protege.editor.core.ui.error.ErrorNotificationLabel;
import org.protege.editor.core.ui.error.SendErrorReportHandler;
import org.protege.editor.core.ui.progress.BackgroundTaskLabel;
import org.protege.editor.core.ui.workspace.*;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ProtegeOWL;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.inference.*;
import org.protege.editor.owl.model.selection.OWLSelectionHistoryManager;
import org.protege.editor.owl.model.selection.OWLSelectionHistoryManagerImpl;
import org.protege.editor.owl.model.selection.OWLSelectionModel;
import org.protege.editor.owl.model.selection.OWLSelectionModelImpl;
import org.protege.editor.owl.ui.OWLEntityCreationPanel;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.protege.editor.owl.ui.inference.*;
import org.protege.editor.owl.ui.navigation.OWLEntityNavPanel;
import org.protege.editor.owl.ui.ontology.OntologySourcesChangedHandlerUI;
import org.protege.editor.owl.ui.preferences.AnnotationPreferences;
import org.protege.editor.owl.ui.renderer.*;
import org.protege.editor.owl.ui.search.SearchDialogPanel;
import org.protege.editor.owl.ui.util.OWLComponentFactory;
import org.protege.editor.owl.ui.util.OWLComponentFactoryImpl;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.OWLEntityCollectingOntologyChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 17, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLWorkspace extends TabbedWorkspace implements SendErrorReportHandler {

    private static final long serialVersionUID = 2340234247624617932L;

    private final Logger logger = LoggerFactory.getLogger(OWLWorkspace.class);

    private static final String WINDOW_MODIFIED = "Window.documentModified";

    private JComboBox<OWLOntology> ontologiesList;

    private ArrayList<OWLEntityDisplayProvider> entityDisplayProviders;

    private OWLIconProvider iconProvider;

    private Map<String, Color> keyWordColorMap;

    private OWLSelectionModel owlSelectionModel;

    private OWLSelectionHistoryManager owlSelectionHistoryManager;

    private OWLModelManagerListener owlModelManagerListener;

    private Set<EventType> reselectionEventTypes = new HashSet<>();

    private ErrorNotificationLabel errorNotificationLabel;

    private BackgroundTaskLabel backgroundTaskLabel;

    private OWLEntityCollectingOntologyChangeListener listener;

    private Set<URI> hiddenAnnotationURIs;

    private OWLComponentFactory owlComponentFactory;

    private JPanel statusArea;

    private JLabel customizedProtege = new JLabel();

    private String altTitle;

    private boolean reasonerManagerStarted = false;

    private PrecomputeAction startReasonerAction = new PrecomputeAction();

    private PrecomputeAction synchronizeReasonerAction = new PrecomputeAction();
    
    private ProtegeOWLAction stopReasonerAction = new StopReasonerAction();

    private ExplainInconsistentOntologyAction explainInconsistentOntologyAction = new ExplainInconsistentOntologyAction();

    private JLabel reasonerStatus = new JLabel();

    private final JCheckBox displayReasonerResults = new JCheckBox("Show Inferences");


    public static final String REASONER_INITIALIZE = "Start reasoner";

    public static final String REASONER_RESYNC = "Synchronize reasoner";
    
    public static final String REASONER_STOP   = "Stop reasoner";

    public static final String REASONER_EXPLAIN = "Explain inconsistent ontology";
    private ErrorLogListener errorLogListener;

    public OWLEditorKit getOWLEditorKit() {
        return (OWLEditorKit) getEditorKit();
    }

    public OWLModelManager getOWLModelManager() {
        return getOWLEditorKit().getModelManager();
    }

    private JDialog searchDialog;

    public void initialise() {
        entityDisplayProviders = new ArrayList<>();
        iconProvider = new OWLIconProviderImpl(getOWLEditorKit().getModelManager());
        owlSelectionModel = new OWLSelectionModelImpl();

        keyWordColorMap = new KeywordColourMap();

        super.initialise();

        errorNotificationLabel = new ErrorNotificationLabel();

        errorLogListener = new ErrorLogListener() {
            @Override
            public void errorLogged() {
                errorNotificationLabel.setVisible(true);
            }

            @Override
            public void errorLogCleared() {
                errorNotificationLabel.setVisible(false);
            }
        };
        ProtegeApplication.getLogManager().addErrorLogListener(errorLogListener);



        backgroundTaskLabel = new BackgroundTaskLabel(ProtegeApplication.getBackgroundTaskManager());



        createActiveOntologyPanel();
        reselectionEventTypes.add(EventType.ACTIVE_ONTOLOGY_CHANGED);
        reselectionEventTypes.add(EventType.ONTOLOGY_RELOADED);
        reselectionEventTypes.add(EventType.ENTITY_RENDERER_CHANGED);
        reselectionEventTypes.add(EventType.ONTOLOGY_VISIBILITY_CHANGED);
        reselectionEventTypes.add(EventType.REASONER_CHANGED);

        hiddenAnnotationURIs = new HashSet<>();
        hiddenAnnotationURIs.addAll(AnnotationPreferences.getHiddenAnnotationURIs());

        owlComponentFactory = new OWLComponentFactoryImpl(getOWLEditorKit());

        final OWLModelManager mngr = getOWLModelManager();

        owlModelManagerListener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                try {
                    handleModelManagerEvent(event.getType());
                }
                catch (Exception t) {
                    logger.warn("An error occurred whilst handling a Model Manager Event: {}", t);
                }
            }
        };
        mngr.addListener(owlModelManagerListener);

        listener = new OWLEntityCollectingOntologyChangeListener() {
            public void ontologiesChanged() {
                verifySelection(getEntities());
            }

            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
                super.ontologiesChanged(changes);
                handleOntologiesChanged(changes);
            }
        };
        mngr.addOntologyChangeListener(listener);

        mngr.getOWLReasonerManager().setReasonerProgressMonitor(new ReasonerProgressUI(getOWLEditorKit()));
        mngr.getOWLReasonerManager().setReasonerExceptionHandler(new UIReasonerExceptionHandler(this));
        reasonerManagerStarted = true;
        updateReasonerStatus(false);
        displayReasonerResults.setSelected(mngr.getOWLReasonerManager().getReasonerPreferences().isShowInferences());
        displayReasonerResults.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ReasonerPreferences prefs = mngr.getOWLReasonerManager().getReasonerPreferences();
                prefs.setShowInferences(displayReasonerResults.isSelected());
            }
        });

        new OntologySourcesChangedHandlerUI(this);
    }


    private void handleOntologiesChanged(List<? extends OWLOntologyChange> changes) {
        boolean reasonerDirty = false;
        boolean ontologyIdsDirty = false;
        for (OWLOntologyChange chg : changes) {
            if (chg instanceof SetOntologyID) {
                ontologyIdsDirty = true;
            }
            else if (chg instanceof ImportChange) {
                reasonerDirty = true;
            }
            else if (chg instanceof OWLAxiomChange) {
                reasonerDirty = true;
            }
        }

        if (reasonerDirty) {
            updateReasonerStatus(true);
        }
        if (ontologyIdsDirty) {
            updateTitleBar();
        }
        updateDirtyFlag();
    }


    private void updateDirtyFlag() {
        WorkspaceManager workspaceManager = ProtegeManager.getInstance().getEditorKitManager().getWorkspaceManager();
        JFrame frame = workspaceManager.getFrame(this);
        Set<OWLOntology> dirtyOntologies = getOWLModelManager().getDirtyOntologies();
        boolean dirty = false;
        for(OWLOntology ont : getOWLModelManager().getOntologies()) {
            if(dirtyOntologies.contains(ont)) {
                dirty = true;
                break;
            }
        }
        frame.getRootPane().putClientProperty(WINDOW_MODIFIED, dirty);
    }


    private void handleModelManagerEvent(EventType type) {
        if (reselectionEventTypes.contains(type)) {
            verifySelection();
        }

        switch (type) {
            case ACTIVE_ONTOLOGY_CHANGED:
                updateTitleBar();
                updateReasonerStatus(false);
                rebuildOntologyDropDown();
                ontologiesList.repaint();
                break;
            case ONTOLOGY_CLASSIFIED:
                updateReasonerStatus(false);
                verifySelection();
                updateReasonerStatus(false);
                break;
            case ABOUT_TO_CLASSIFY:
            case REASONER_CHANGED:
                updateReasonerStatus(false);
                break;
            case ONTOLOGY_LOADED:
            case ONTOLOGY_CREATED:
                if (getTabCount() > 0) {
                    setSelectedTab(0);
                }
                break;
            case ENTITY_RENDERER_CHANGED:
            case ONTOLOGY_RELOADED:
                rebuildOntologyDropDown();
                refreshComponents();
                break;
            case ONTOLOGY_SAVED:
                updateDirtyFlag();
                updateTitleBar();
                break;
            case ENTITY_RENDERING_CHANGED:
            case ONTOLOGY_VISIBILITY_CHANGED:
                break;
        }
    }


    public void refreshComponents() {
        refreshComponents(this);
    }

    public void refreshComponents(Component component) {
        if (component instanceof Container) {
            Container cont = (Container) component;
            for (Component childComp : cont.getComponents()) {
                refreshComponents(childComp);
            }
        }
        if (isComponentFontSizeSensitive(component)) {
            Font f = component.getFont();
            if (f != null) {
                component.setFont(f.deriveFont(f.getStyle(), OWLRendererPreferences.getInstance().getFontSize()));
            }
        }
        if (component instanceof RefreshableComponent) {
            ((RefreshableComponent) component).refreshComponent();
        }
    }

    private boolean isComponentFontSizeSensitive(Component component) {
        return component instanceof JTextComponent || component instanceof JLabel || component instanceof JTree || component instanceof JList || component instanceof JTable;
    }

    protected void verifySelection(Set<? extends OWLEntity> entities) {
        Set<OWLEntity> unreferencedEntities = new HashSet<>(entities);
        for (OWLEntity entity : entities) {
            if (entity != null) {
                for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
                    if (ont.containsEntityInSignature(entity)) {
                        unreferencedEntities.remove(entity);
                        break;
                    }
                }
            }
        }
        for (OWLEntity entity : unreferencedEntities) {
            getOWLSelectionModel().clearLastSelectedEntity(entity);
        }
    }


    protected void verifySelection() {
        logger.debug("--- Verifying selection ---");
        OWLSelectionModel selectionModel = getOWLSelectionModel();

        OWLClass lastSelectedClass = selectionModel.getLastSelectedClass();
        selectionModel.setSelectedEntity(lastSelectedClass);
        logger.debug("Last selected class: {}", lastSelectedClass);

        OWLObjectProperty lastSelectedObjectProperty = selectionModel.getLastSelectedObjectProperty();
        selectionModel.setSelectedEntity(lastSelectedObjectProperty);
        logger.debug("Last selected object property: {}", lastSelectedObjectProperty);

        OWLDataProperty lastSelectedDataProperty = selectionModel.getLastSelectedDataProperty();
        selectionModel.setSelectedEntity(lastSelectedDataProperty);
        logger.debug("Last selected data property: {}", lastSelectedDataProperty);

        OWLAnnotationProperty lastSelectedAnnotationProperty = selectionModel.getLastSelectedAnnotationProperty();
        selectionModel.setSelectedEntity(lastSelectedAnnotationProperty);
        logger.debug("Last selected annotation property: " + lastSelectedAnnotationProperty);

        OWLNamedIndividual lastSelectedIndividual = selectionModel.getLastSelectedIndividual();
        selectionModel.setSelectedEntity(lastSelectedIndividual);
        logger.debug("Last selected individual: {}", lastSelectedIndividual);

        OWLDatatype lastSelectedDatatype = selectionModel.getLastSelectedDatatype();
        selectionModel.setSelectedEntity(lastSelectedDatatype);
        logger.debug("Last selected datatype: {}", lastSelectedDatatype);

        OWLEntity selectedEntity = selectionModel.getSelectedEntity();
        selectionModel.setSelectedEntity(selectedEntity);
        logger.debug("Last selected entity: {}", selectedEntity);

        verifySelection(CollectionFactory.createSet(lastSelectedClass, lastSelectedDataProperty, lastSelectedObjectProperty, lastSelectedAnnotationProperty, lastSelectedIndividual, lastSelectedDatatype, selectedEntity));
        logger.debug("---------------------------");
    }


    public boolean isHiddenAnnotationURI(URI annotationURI) {
        return hiddenAnnotationURIs.contains(annotationURI);
    }


    public Set<URI> getHiddenAnnotationURIs() {
        return Collections.unmodifiableSet(hiddenAnnotationURIs);
    }

    public void setCustomizedBy(String customizedText) {
        customizedProtege.setText(customizedText);
        statusArea.repaint();
    }

    public void setHiddenAnnotationURI(URI annotationURI, boolean hidden) {
        boolean changed;
        if (hidden) {
            changed = hiddenAnnotationURIs.add(annotationURI);
        }
        else {
            changed = hiddenAnnotationURIs.remove(annotationURI);
        }
        if (changed) {
            AnnotationPreferences.setHiddenAnnotationURIs(hiddenAnnotationURIs);
            getOWLEditorKit().getModelManager().fireEvent(EventType.ACTIVE_ONTOLOGY_CHANGED);
        }
    }


    public void setHiddenAnnotationURIs(Set<URI> hiddenURIs) {
        if (!hiddenURIs.equals(hiddenAnnotationURIs)) {
            hiddenAnnotationURIs.clear();
            hiddenAnnotationURIs.addAll(hiddenURIs);
            AnnotationPreferences.setHiddenAnnotationURIs(hiddenAnnotationURIs);
            getOWLEditorKit().getModelManager().fireEvent(EventType.ACTIVE_ONTOLOGY_CHANGED);
        }
    }

    private List<ProtegeAction> extraReasonerMenuActions;

    protected void initialiseExtraMenuItems(JMenuBar menuBar) {
        super.initialiseExtraMenuItems(menuBar);

        getOntologiesMenu(menuBar);
        this.extraReasonerMenuActions = initialiseExtraReasonerMenuActions();

        rebuildReasonerMenu(menuBar);
        addReasonerListener(menuBar);
        updateTitleBar();

        JMenu windowMenu = getWindowMenu(menuBar);
        windowMenu.addSeparator();
        windowMenu.add(new AbstractAction("Refresh user interface") {


            public void actionPerformed(ActionEvent e) {
                refreshComponents();
            }
        });
    }

    private class ExtraReasonerMenuActionPlugin extends ProtegeActionPluginJPFImpl {
        protected ExtraReasonerMenuActionPlugin(EditorKit editorKit, IExtension extension) {
            super(editorKit, extension);
        }

        public String getAccelerator() {
            return PluginUtilities.getAttribute(getIExtension(), "accelerator");
        }
    }

    private class ExtraReasonerMenuActionPluginLoader extends AbstractPluginLoader<ExtraReasonerMenuActionPlugin> {

        /**
         * Creates a loader that will load (a subset) of the plugins
         * that extend the specified plugin at the specified plugin
         * extension point.
         */
        public ExtraReasonerMenuActionPluginLoader() {
            super(ProtegeOWL.ID, "ExtraReasonerMenuAction");
        }

        /**
         * This method needs to be overriden to create an instance
         * of the desired plugin, based on the plugin <code>Extension</code>
         *
         * @param extension The <code>Extension</code> that describes the
         *                  Java Plugin Framework extension.
         * @return A plugin object (typically some sort of wrapper around
         * the extension)
         */
        @Override
        protected ExtraReasonerMenuActionPlugin createInstance(IExtension extension) {

            return new ExtraReasonerMenuActionPlugin(getOWLEditorKit(), extension);
        }
    }

    private List<ProtegeAction> initialiseExtraReasonerMenuActions() {
        List<ProtegeAction> result = new ArrayList<>();
        ExtraReasonerMenuActionPluginLoader loader = new ExtraReasonerMenuActionPluginLoader();
        for (ExtraReasonerMenuActionPlugin extraReasonerMenuActionPlugin : loader.getPlugins()) {
            try {
                ProtegeAction action = extraReasonerMenuActionPlugin.newInstance();
                action.setEditorKit(getOWLEditorKit());
                String name = extraReasonerMenuActionPlugin.getName();
                if (name == null) {
                    logger.warn("Extra reasoner menu action plugin has no name: " + extraReasonerMenuActionPlugin);
                }
                action.putValue(Action.NAME, name);
                String toolTipText = extraReasonerMenuActionPlugin.getToolTipText();
                if (toolTipText != null) {
                    action.putValue(Action.SHORT_DESCRIPTION, toolTipText);
                }
                String accelerator = extraReasonerMenuActionPlugin.getAccelerator();
                if(accelerator != null) {
                    action.putValue(Action.ACCELERATOR_KEY,accelerator);
                }
                result.add(action);
                logger.debug("action = " + action);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                logger.error("Error whilst loading menu plugin", e); //To change body of catch statement use File | Settings | File Templates.
            }

        }

        return result;
    }

    private void rebuildReasonerMenu(JMenuBar menuBar) {
        final OWLModelManager mngr = getOWLModelManager();

        JMenu reasonerMenu = getReasonerMenu(menuBar);

        reasonerMenu.removeAll();

        startReasonerAction.setEditorKit(getOWLEditorKit());
        startReasonerAction.putValue(Action.NAME, REASONER_INITIALIZE);
        reasonerMenu.add(startReasonerAction);

        synchronizeReasonerAction.setEditorKit(getOWLEditorKit());
        synchronizeReasonerAction.putValue(Action.NAME, REASONER_RESYNC);
        reasonerMenu.add(synchronizeReasonerAction);
        
        stopReasonerAction.setEditorKit(getOWLEditorKit());
        stopReasonerAction.putValue(Action.NAME, REASONER_STOP);
        reasonerMenu.add(stopReasonerAction);

        explainInconsistentOntologyAction.setEditorKit(getOWLEditorKit());
        explainInconsistentOntologyAction.putValue(Action.NAME, REASONER_EXPLAIN);
        explainInconsistentOntologyAction.setEnabled(false);
        reasonerMenu.add(explainInconsistentOntologyAction);

        ConfigureReasonerAction configureAction = new ConfigureReasonerAction();
        configureAction.setEditorKit(getOWLEditorKit());
        configureAction.putValue(Action.NAME, "Configure...");
        reasonerMenu.add(configureAction);

         if (extraReasonerMenuActions != null && extraReasonerMenuActions.size() > 0) {
             reasonerMenu.addSeparator();
             for (ProtegeAction action : extraReasonerMenuActions) {
                 reasonerMenu.add(action);
             }
         }

        reasonerMenu.addSeparator();

        ButtonGroup bg = new ButtonGroup();
        Set<ProtegeOWLReasonerInfo> factories = mngr.getOWLReasonerManager().getInstalledReasonerFactories();
        List<ProtegeOWLReasonerInfo> factoriesList = new ArrayList<>(factories);
        Collections.sort(factoriesList, new ReasonerInfoComparator());
        for (final ProtegeOWLReasonerInfo plugin : factoriesList) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(plugin.getReasonerName());
            item.setSelected(mngr.getOWLReasonerManager().getCurrentReasonerFactoryId().equals(plugin.getReasonerId()));
            reasonerMenu.add(item);
            bg.add(item);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mngr.getOWLReasonerManager().setCurrentReasonerFactoryId(plugin.getReasonerId());
                }
            });
        }
    }

    private void addReasonerListener(final JMenuBar menuBar) {
        final IExtensionRegistry registry = PluginUtilities.getInstance().getExtensionRegistry();
        final IExtensionPoint point = registry.getExtensionPoint(ProtegeOWL.ID, ProtegeOWLReasonerPlugin.REASONER_PLUGIN_TYPE_ID);

        registry.addListener(new IRegistryEventListener() {

            public void added(IExtension[] extensions) {
                OWLReasonerManagerImpl reasonerManager = (OWLReasonerManagerImpl) getOWLModelManager().getOWLReasonerManager();
                Set<ProtegeOWLReasonerPlugin> plugins = new HashSet<>();
                for (IExtension extension : extensions) {
                    plugins.add(new ProtegeOWLReasonerPluginJPFImpl(getOWLModelManager(), extension));
                }
                reasonerManager.addReasonerFactories(plugins);
                rebuildReasonerMenu(menuBar);
                menuBar.repaint();
            }

            public void added(IExtensionPoint[] extensionPoints) {
            }

            public void removed(IExtension[] extensions) {
            }

            public void removed(IExtensionPoint[] extensionPoints) {
            }

        }, point.getUniqueIdentifier());

    }

    private JMenu getOntologiesMenu(JMenuBar menuBar) {
        return getMenu(menuBar, "Ontologies");
    }


    private static JMenu getReasonerMenu(JMenuBar menuBar) {
        return getMenu(menuBar, "Reasoner");
    }


    private static JMenu getWindowMenu(JMenuBar menuBar) {
        return getMenu(menuBar, "Window");
    }

    private static JMenu getMenu(JMenuBar menuBar, String name) {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu != null) {
                if (menu.getText() != null) {
                    if (menu.getText().equals(name)) {
                        return menu;
                    }
                }
            }
        }
        return null;
    }



    private void createActiveOntologyPanel() {

        JPanel topBarPanel = new JPanel(new GridBagLayout());
        topBarPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 3, 10));
        topBarPanel.add(new OWLEntityNavPanel(getOWLEditorKit()),
                new GridBagConstraints(
                        0, 0,
                        1, 1,
                        0, 0,
                        GridBagConstraints.BASELINE, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 2),
                        0, 0));

        final OWLModelManager mngr = getOWLModelManager();

        // Install the active ontology combo box
        ontologiesList = new JComboBox<>();
        ontologiesList.setToolTipText("Active ontology");
        ontologiesList.setRenderer(new OWLOntologyCellRenderer(getOWLEditorKit()));
        rebuildOntologyDropDown();

        topBarPanel.add(ontologiesList, new GridBagConstraints(
                1, 0,
                1, 1,
                100, 0,
                GridBagConstraints.BASELINE,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0),
                0, 0
        ));

        ontologiesList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OWLOntology ont = (OWLOntology) ontologiesList.getSelectedItem();
                if (ont != null) {
                    mngr.setActiveOntology(ont);
                }
            }
        });

        JButton searchButton = new JButton(new AbstractAction("Search...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSearchDialog();
            }
        });
        topBarPanel.add(searchButton, new GridBagConstraints(
                2, 0,
                1, 1,
                0, 0,
                GridBagConstraints.BASELINE,
                GridBagConstraints.NONE,
                new Insets(0, 2, 0, 2),
                0, 0
        ));

        topBarPanel.add(backgroundTaskLabel,
                new GridBagConstraints(
                        3, 0,
                        1, 1,
                        0, 0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0),
                        0, 0
                ));
        topBarPanel.add(errorNotificationLabel,
                new GridBagConstraints(
                        4, 0,
                        1, 1,
                        0, 0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0),
                        0, 0
                )
                );

        add(topBarPanel, BorderLayout.NORTH);

//        KeyStroke findKeyStroke = KeyStroke.getKeyStroke(
//                KeyEvent.VK_F,
//                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
//        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(findKeyStroke, "SHOW_SEARCH_DIALOG");
//        getActionMap().put("SHOW_SEARCH_DIALOG", new AbstractAction() {
//            /**
//             *
//             */
//            private static final long serialVersionUID = -2205711779338124168L;
//
//            public void actionPerformed(ActionEvent e) {
//                showSearchDialog();
//            }
//        });
        updateTitleBar();
    }

    public void showSearchDialog() {
        if(searchDialog == null) {
            searchDialog = SearchDialogPanel.createDialog(this, getOWLEditorKit());
            Point workspaceLocation = getLocation();
            Dimension workspaceSize = getSize();
            SwingUtilities.convertPointToScreen(workspaceLocation, this);
            workspaceLocation.translate(workspaceSize.width - searchDialog.getWidth() - 10, 0);
            searchDialog.setLocation(workspaceLocation);
        }
        searchDialog.setVisible(true);
    }

    public void setTitle(String title) {
        altTitle = title;
        updateTitleBar();
    }

    protected String getTitle() {
        if (altTitle != null) {
            return altTitle;
        }
        final OWLModelManager mngr = getOWLModelManager();
        OWLOntology activeOntology = mngr.getActiveOntology();
        if (activeOntology == null) {
            return null;
        }
        URI locURI = mngr.getOntologyPhysicalURI(activeOntology);
        String location = "*";
        if (locURI != null) {
            if (!locURI.toString().isEmpty()) {
                location = locURI.toString();
                if ("file".equals(locURI.getScheme())) {
                    location = new File(locURI).getPath();
                }
            }
        }

        String ontShortName = mngr.getRendering(activeOntology);
        Optional<IRI> defaultDocumentIRI = activeOntology.getOntologyID().getDefaultDocumentIRI();
        String documentIRIPart = "";
        if (defaultDocumentIRI.isPresent()) {
            documentIRIPart = " (" + defaultDocumentIRI.get() + ") ";
        }
        return ontShortName + documentIRIPart + " : [" + location + "]";
    }


    private void updateTitleBar() {
        Frame f = ProtegeManager.getInstance().getFrame(this);
        if (f != null) {
            f.setTitle(getTitle());
        }
        ontologiesList.repaint();
    }

    private void updateReasonerStatus(boolean changesInProgress) {
        if (!reasonerManagerStarted) {
            return;
        }
        OWLReasonerManager reasonerManager = getOWLEditorKit().getOWLModelManager().getOWLReasonerManager();
        ReasonerStatus newStatus;
        try {
            newStatus = reasonerManager.getReasonerStatus();
        }
        catch (ReasonerDiedException reasonerDied) {
            newStatus = ReasonerStatus.REASONER_NOT_INITIALIZED;
            ReasonerUtilities.warnThatReasonerDied(null, reasonerDied);
        }
        if (changesInProgress && (newStatus == ReasonerStatus.INITIALIZED || newStatus == ReasonerStatus.INCONSISTENT) && reasonerManager.getCurrentReasoner().getBufferingMode() == BufferingMode.BUFFERING) {
            newStatus = ReasonerStatus.OUT_OF_SYNC;
        }
        updateReasonerStatus(newStatus);
    }

    private void updateReasonerStatus(ReasonerStatus status) {
        reasonerStatus.setText(status.getDescription());
        
        startReasonerAction.setEnabled(status.isEnableInitialization());
        startReasonerAction.putValue(Action.SHORT_DESCRIPTION, status.getInitializationTooltip());
        
        synchronizeReasonerAction.setEnabled(status.isEnableSynchronization());
        synchronizeReasonerAction.putValue(Action.SHORT_DESCRIPTION, status.getSynchronizationTooltip());
        
        stopReasonerAction.setEnabled(status.isEnableStop());
        
        explainInconsistentOntologyAction.setEnabled(status == ReasonerStatus.INCONSISTENT);

        KeyStroke shortcut = KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        startReasonerAction.putValue(AbstractAction.ACCELERATOR_KEY, status.isEnableInitialization() ? shortcut : null);
        synchronizeReasonerAction.putValue(AbstractAction.ACCELERATOR_KEY, status.isEnableSynchronization() ? shortcut : null);
    }


    public void displayOWLEntity(OWLEntity owlEntity) {
        OWLEntityDisplayProvider candidate = null;
        for (OWLEntityDisplayProvider provider : entityDisplayProviders) {
            if (provider.canDisplay(owlEntity)) {
                if (candidate == null) {
                    candidate = provider;
                }
                if (provider.getDisplayComponent().isShowing()) {
                    candidate = provider;
                    break;
                }
            }
        }
        if (candidate != null) {
            JComponent component = candidate.getDisplayComponent();
            if (component != null) {
                bringComponentToFront(component);
            }
        }
    }


    public CustomWorkspaceTabsManager getCustomTabsManager() {
        return new OWLCustomWorkspaceTabsManager();
    }


    private static void bringComponentToFront(Component component) {
        if (component.isShowing()) {
            return;
        }
        Component parent = component.getParent();
        if (parent == null) {
            return;
        }
        if (parent instanceof JTabbedPane) {
            ((JTabbedPane) parent).setSelectedComponent(component);
        }
        bringComponentToFront(parent);
    }


    public void registerOWLEntityDisplayProvider(OWLEntityDisplayProvider provider) {
        entityDisplayProviders.add(provider);
    }


    public void unregisterOWLEntityDisplayProvider(OWLEntityDisplayProvider provider) {
        entityDisplayProviders.remove(provider);
    }


    public void dispose() {
        // Save our workspace!
        super.dispose();

        owlComponentFactory.dispose();

        ProtegeApplication.getLogManager().removeErrorLogListener(errorLogListener);

        getOWLModelManager().removeListener(owlModelManagerListener);
        getOWLModelManager().removeOntologyChangeListener(listener);
    }


    private void rebuildOntologyDropDown() {
        try {
            TreeSet<OWLOntology> ts = new TreeSet<>(getOWLModelManager().getOWLObjectComparator());
            ts.addAll(getOWLModelManager().getOntologies());
            ontologiesList.setModel(new DefaultComboBoxModel<>(ts.toArray(new OWLOntology[ts.size()])));
            ontologiesList.setSelectedItem(getOWLModelManager().getActiveOntology());
        }
        catch (Exception e) {
            logger.error("An error occurred whilst building the ontology list: {}", e);
        }
    }


    public OWLIconProvider getOWLIconProvider() {
        return iconProvider;
    }


    public void setOWLIconProvider(OWLIconProvider provider) {
        this.iconProvider = provider;
    }


    public OWLCellRenderer createOWLCellRenderer() {
        return createOWLCellRenderer(true, true);
    }


    public OWLCellRenderer createOWLCellRenderer(boolean renderExpression, boolean renderIcon) {
        return new OWLCellRenderer(getOWLEditorKit(), renderExpression, renderIcon);
    }


    public Map<String, Color> getKeyWordColorMap() {
        return keyWordColorMap;
    }


    public OWLSelectionModel getOWLSelectionModel() {
        return owlSelectionModel;
    }


    public OWLSelectionHistoryManager getOWLSelectionHistoryManager() {
        if (owlSelectionHistoryManager == null) {
            owlSelectionHistoryManager = new OWLSelectionHistoryManagerImpl(getOWLSelectionModel());
        }
        return owlSelectionHistoryManager;
    }


    public OWLEntityCreationSet<OWLClass> createOWLClass() {
        return OWLEntityCreationPanel.showDialog(getOWLEditorKit(), "Please enter a class name", OWLClass.class);
    }


    public OWLEntityCreationSet<OWLObjectProperty> createOWLObjectProperty() {
        return OWLEntityCreationPanel.showDialog(getOWLEditorKit(), "Please enter an object property name", OWLObjectProperty.class);
    }


    public OWLEntityCreationSet<OWLDataProperty> createOWLDataProperty() {
        return OWLEntityCreationPanel.showDialog(getOWLEditorKit(), "Please enter a data property name", OWLDataProperty.class);
    }


    public OWLEntityCreationSet<OWLAnnotationProperty> createOWLAnnotationProperty() {
        return OWLEntityCreationPanel.showDialog(getOWLEditorKit(), "Please enter an annotation property name", OWLAnnotationProperty.class);
    }


    public OWLEntityCreationSet<OWLNamedIndividual> createOWLIndividual() {
        return OWLEntityCreationPanel.showDialog(getOWLEditorKit(), "Please enter an individual name", OWLNamedIndividual.class);
    }


    public OWLEntityCreationSet<OWLDatatype> createOWLDatatype() {
        return OWLEntityCreationPanel.showDialog(getOWLEditorKit(), "Please enter a datatype name", OWLDatatype.class);
    }


    public OWLComponentFactory getOWLComponentFactory() {
        return owlComponentFactory;
    }


    public static KeyStroke getCreateSubKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    }


    public static KeyStroke getCreateSibKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    }


    public boolean sendErrorReport(ErrorLog errorLog) {
        return true;
    }

    public JComponent getStatusArea() {
        if (statusArea == null) {
            statusArea = new JPanel();
            statusArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 2));
            statusArea.setLayout(new BoxLayout(statusArea, BoxLayout.X_AXIS));
            statusArea.add(Box.createHorizontalGlue());
            statusArea.add(customizedProtege);
            statusArea.add(Box.createHorizontalGlue());
            statusArea.add(reasonerStatus);
            statusArea.add(Box.createHorizontalStrut(15));
            statusArea.add(displayReasonerResults);
            statusArea.add(Box.createHorizontalStrut(20));
        }
        return statusArea;
    }

    public WorkspaceTab createWorkspaceTab(final String name) {
        final OWLWorkspaceViewsTab tab = new OWLWorkspaceViewsTab();
        tab.setup(new WorkspaceTabPlugin() {
            public TabbedWorkspace getWorkspace() {
                return OWLWorkspace.this;
            }


            public String getLabel() {
                return name;
            }


            public Icon getIcon() {
                return null;
            }


            public String getIndex() {
                return "Z";
            }


            public URL getDefaultViewConfigFile() {
                try {
                    return new File(getId() + "-config.xml").toURI().toURL();
                }
                catch (MalformedURLException uriex) {
                    logger.warn("The default view configuration file is malformed: " + uriex.getMessage());
                }
                return null;
            }


            public String getId() {
                return "WorkspaceTab" + System.nanoTime();
            }


            public String getDocumentation() {
                return null;
            }


            public WorkspaceTab newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
                return tab;
            }
        });

        return tab;
    }
}
