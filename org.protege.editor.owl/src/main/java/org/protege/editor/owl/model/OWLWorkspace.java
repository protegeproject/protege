package org.protege.editor.owl.model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.ui.RefreshableComponent;
import org.protege.editor.core.ui.error.ErrorLog;
import org.protege.editor.core.ui.error.ErrorNotificationLabel;
import org.protege.editor.core.ui.error.SendErrorReportHandler;
import org.protege.editor.core.ui.progress.BackgroundTaskLabel;
import org.protege.editor.core.ui.workspace.CustomWorkspaceTabsManager;
import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.WorkspaceManager;
import org.protege.editor.core.ui.workspace.WorkspaceTab;
import org.protege.editor.core.ui.workspace.WorkspaceTabPlugin;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ProtegeOWL;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.OWLReasonerManagerImpl;
import org.protege.editor.owl.model.inference.ProtegeOWLReasonerInfo;
import org.protege.editor.owl.model.inference.ProtegeOWLReasonerPlugin;
import org.protege.editor.owl.model.inference.ProtegeOWLReasonerPluginJPFImpl;
import org.protege.editor.owl.model.inference.ReasonerDiedException;
import org.protege.editor.owl.model.inference.ReasonerInfoComparator;
import org.protege.editor.owl.model.inference.ReasonerPreferences;
import org.protege.editor.owl.model.inference.ReasonerStatus;
import org.protege.editor.owl.model.inference.ReasonerUtilities;
import org.protege.editor.owl.model.selection.OWLSelectionHistoryManager;
import org.protege.editor.owl.model.selection.OWLSelectionHistoryManagerImpl;
import org.protege.editor.owl.model.selection.OWLSelectionModel;
import org.protege.editor.owl.model.selection.OWLSelectionModelImpl;
import org.protege.editor.owl.ui.OWLEntityCreationPanel;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.protege.editor.owl.ui.find.EntityFinderField;
import org.protege.editor.owl.ui.inference.ConfigureReasonerAction;
import org.protege.editor.owl.ui.inference.ExplainInconsistentOntologyAction;
import org.protege.editor.owl.ui.inference.PrecomputeAction;
import org.protege.editor.owl.ui.inference.ReasonerProgressUI;
import org.protege.editor.owl.ui.inference.StopReasonerAction;
import org.protege.editor.owl.ui.navigation.OWLEntityNavPanel;
import org.protege.editor.owl.ui.ontology.OntologySourcesChangedHandlerUI;
import org.protege.editor.owl.ui.preferences.AnnotationPreferences;
import org.protege.editor.owl.ui.renderer.KeywordColourMap;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.renderer.OWLIconProvider;
import org.protege.editor.owl.ui.renderer.OWLIconProviderImpl;
import org.protege.editor.owl.ui.renderer.OWLOntologyCellRenderer;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.editor.owl.ui.util.OWLComponentFactory;
import org.protege.editor.owl.ui.util.OWLComponentFactoryImpl;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.ImportChange;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.OWLEntityCollectingOntologyChangeListener;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;


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

    private Logger logger = Logger.getLogger(OWLWorkspace.class);

    private static final String WINDOW_MODIFIED = "Window.documentModified";

    private static final int FINDER_BORDER = 1;

    private static final int FINDER_MIN_WIDTH = 250;


    private JComboBox ontologiesList;

    private ArrayList<OWLEntityDisplayProvider> entityDisplayProviders;

    private OWLIconProvider iconProvider;

    private Map<String, Color> keyWordColorMap;

    private OWLSelectionModel owlSelectionModel;

    private OWLSelectionHistoryManager owlSelectionHistoryManager;

    private List<URI> defaultAnnotationProperties;

    private OWLModelManagerListener owlModelManagerListener;

    private Set<EventType> reselectionEventTypes = new HashSet<EventType>();

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

    private JCheckBox displayReasonerResults = new JCheckBox("Show Inferences");


    public static final String REASONER_INITIALIZE = "Start reasoner";

    public static final String REASONER_RESYNC = "Synchronize reasoner";
    
    public static final String REASONER_STOP   = "Stop reasoner";

    public static final String REASONER_EXPLAIN = "Explain inconsistent ontology";

    public OWLEditorKit getOWLEditorKit() {
        return (OWLEditorKit) getEditorKit();
    }


    public OWLModelManager getOWLModelManager() {
        return getOWLEditorKit().getModelManager();
    }


    public void initialise() {
        entityDisplayProviders = new ArrayList<OWLEntityDisplayProvider>();
        iconProvider = new OWLIconProviderImpl(getOWLEditorKit().getModelManager());
        owlSelectionModel = new OWLSelectionModelImpl();

        keyWordColorMap = new KeywordColourMap();

        defaultAnnotationProperties = new ArrayList<URI>();
        defaultAnnotationProperties.add(OWLRDFVocabulary.RDFS_COMMENT.getURI());

        super.initialise();

        errorNotificationLabel = new ErrorNotificationLabel(ProtegeApplication.getErrorLog(), this);

        backgroundTaskLabel = new BackgroundTaskLabel(ProtegeApplication.getBackgroundTaskManager());

        createActiveOntologyPanel();
        reselectionEventTypes.add(EventType.ACTIVE_ONTOLOGY_CHANGED);
        reselectionEventTypes.add(EventType.ONTOLOGY_RELOADED);
        reselectionEventTypes.add(EventType.ENTITY_RENDERER_CHANGED);
        reselectionEventTypes.add(EventType.ONTOLOGY_VISIBILITY_CHANGED);
        reselectionEventTypes.add(EventType.REASONER_CHANGED);

        hiddenAnnotationURIs = new HashSet<URI>();
        hiddenAnnotationURIs.addAll(AnnotationPreferences.getHiddenAnnotationURIs());

        owlComponentFactory = new OWLComponentFactoryImpl(getOWLEditorKit());

        final OWLModelManager mngr = getOWLModelManager();

        owlModelManagerListener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                try {
                    handleModelManagerEvent(event.getType());
                }
                catch (Exception t) {
                    ProtegeApplication.getErrorLog().logError(t);
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
            logger.debug("Reselection event..... verifying selections.");
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
        Set<OWLEntity> unreferencedEntities = new HashSet<OWLEntity>(entities);
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
        OWLSelectionModel selectionModel = getOWLSelectionModel();
        OWLClass lastSelectedClass = selectionModel.getLastSelectedClass();
        OWLObjectProperty lastSelectedObjectProperty = selectionModel.getLastSelectedObjectProperty();
        OWLDataProperty lastSelectedDataProperty = selectionModel.getLastSelectedDataProperty();
        OWLAnnotationProperty lastSelectedAnnotationProperty = selectionModel.getLastSelectedAnnotationProperty();
        OWLNamedIndividual lastSelectedIndividual = selectionModel.getLastSelectedIndividual();
        OWLDatatype lastSelectedDatatype = selectionModel.getLastSelectedDatatype();
        OWLEntity selectedEntity = selectionModel.getSelectedEntity();

        selectionModel.setSelectedEntity(lastSelectedClass);
        selectionModel.setSelectedEntity(lastSelectedObjectProperty);
        selectionModel.setSelectedEntity(lastSelectedDataProperty);
        selectionModel.setSelectedEntity(lastSelectedAnnotationProperty);
        selectionModel.setSelectedEntity(lastSelectedIndividual);
        selectionModel.setSelectedEntity(lastSelectedDatatype);
        selectionModel.setSelectedEntity(selectedEntity);

        verifySelection(CollectionFactory.createSet(lastSelectedClass, lastSelectedDataProperty, lastSelectedObjectProperty, lastSelectedAnnotationProperty, lastSelectedIndividual, lastSelectedDatatype, selectedEntity));
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


    protected void initialiseExtraMenuItems(JMenuBar menuBar) {
        super.initialiseExtraMenuItems(menuBar);

        getOntologiesMenu(menuBar);
        rebuildReasonerMenu(menuBar);
        addReasonerListener(menuBar);
        updateTitleBar();

        JMenu windowMenu = getWindowMenu(menuBar);
        windowMenu.addSeparator();
        windowMenu.add(new AbstractAction("Refresh user interface") {

            /**
             *
             */
            private static final long serialVersionUID = 9136219526373256639L;

            public void actionPerformed(ActionEvent e) {
                refreshComponents();
            }
        });
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

        reasonerMenu.addSeparator();

        ButtonGroup bg = new ButtonGroup();
        Set<ProtegeOWLReasonerInfo> factories = mngr.getOWLReasonerManager().getInstalledReasonerFactories();
        List<ProtegeOWLReasonerInfo> factoriesList = new ArrayList<ProtegeOWLReasonerInfo>(factories);
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
                Set<ProtegeOWLReasonerPlugin> plugins = new HashSet<ProtegeOWLReasonerPlugin>();
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
            if (menuBar.getMenu(i).getText() != null) {
                if (menuBar.getMenu(i).getText().equals(name)) {
                    return menuBar.getMenu(i);
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
        ontologiesList = new JComboBox();
        ontologiesList.setToolTipText("Active ontology");
        ontologiesList.setRenderer(new OWLOntologyCellRenderer(getOWLEditorKit()));
        rebuildOntologyDropDown();

        topBarPanel.add(ontologiesList, new GridBagConstraints(
                1, 0,
                1, 1,
                100, 0,
                GridBagConstraints.BASELINE,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 2, 0, 2),
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

        final EntityFinderField entityFinderField = new EntityFinderField(this, getOWLEditorKit());
        entityFinderField.setPreferredSize(new Dimension(250, 30));
        entityFinderField.setMinimumSize(new Dimension(250, 30));
        topBarPanel.add(entityFinderField, new GridBagConstraints(
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

// Find focus accelerator
        KeyStroke findKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(findKeyStroke, "FOCUS_FIND");
        getActionMap().put("FOCUS_FIND", new AbstractAction() {
            /**
             *
             */
            private static final long serialVersionUID = -2205711779338124168L;

            public void actionPerformed(ActionEvent e) {
                entityFinderField.requestFocus();
            }
        });
        updateTitleBar();
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
        IRI defaultDocumentIRI = activeOntology.getOntologyID().getDefaultDocumentIRI();
        String documentIRIPart = "";
        if (defaultDocumentIRI != null) {
            documentIRIPart = " (" + defaultDocumentIRI + ") ";
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

        getOWLModelManager().removeListener(owlModelManagerListener);
        getOWLModelManager().removeOntologyChangeListener(listener);
    }


    private void rebuildOntologyDropDown() {
        try {
            TreeSet<OWLOntology> ts = new TreeSet<OWLOntology>(getOWLModelManager().getOWLObjectComparator());
            ts.addAll(getOWLModelManager().getOntologies());
            ontologiesList.setModel(new DefaultComboBoxModel(ts.toArray()));
            ontologiesList.setSelectedItem(getOWLModelManager().getActiveOntology());
        }
        catch (Exception e) {
            ProtegeApplication.getErrorLog().logError(e);
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
                    ProtegeApplication.getErrorLog().logError(uriex);
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
