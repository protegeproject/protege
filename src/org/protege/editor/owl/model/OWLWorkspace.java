package org.protege.editor.owl.model;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.RefreshableComponent;
import org.protege.editor.core.ui.error.ErrorLog;
import org.protege.editor.core.ui.error.ErrorNotificationLabel;
import org.protege.editor.core.ui.error.SendErrorReportHandler;
import org.protege.editor.core.ui.progress.BackgroundTaskLabel;
import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.core.ui.util.OSUtils;
import org.protege.editor.core.ui.workspace.CustomWorkspaceTabsManager;
import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.WorkspaceTab;
import org.protege.editor.core.ui.workspace.WorkspaceTabPlugin;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.inference.ProtegeOWLReasonerFactory;
import org.protege.editor.owl.model.selection.OWLSelectionHistoryManager;
import org.protege.editor.owl.model.selection.OWLSelectionHistoryManagerImpl;
import org.protege.editor.owl.model.selection.OWLSelectionModel;
import org.protege.editor.owl.model.selection.OWLSelectionModelImpl;
import org.protege.editor.owl.model.selection.ontologies.OntologySelectionStrategy;
import org.protege.editor.owl.ui.OWLEntityCreationPanel;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
import org.protege.editor.owl.ui.find.EntityFinderField;
import org.protege.editor.owl.ui.inference.ReasonerProgressUI;
import org.protege.editor.owl.ui.navigation.OWLEntityNavPanel;
import org.protege.editor.owl.ui.ontology.OntologySourcesChangedHandlerUI;
import org.protege.editor.owl.ui.preferences.AnnotationPreferences;
import org.protege.editor.owl.ui.renderer.*;
import org.protege.editor.owl.ui.util.OWLComponentFactory;
import org.protege.editor.owl.ui.util.OWLComponentFactoryImpl;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.CollectionFactory;
import org.semanticweb.owl.util.OWLEntityCollectingOntologyChangeListener;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

import javax.swing.*;
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

    private Logger logger = Logger.getLogger(OWLWorkspace.class);

    private static final String WINDOW_MODIFIED = "windowModified";
    private static final int FINDER_BORDER = 2;
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

    private JMenu ontologiesMenu;

    private OWLComponentFactory owlComponentFactory;


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
        reselectionEventTypes.add(EventType.ENTITY_RENDERER_CHANGED);
        reselectionEventTypes.add(EventType.ONTOLOGY_VISIBILITY_CHANGED);
        reselectionEventTypes.add(EventType.REASONER_CHANGED);

        hiddenAnnotationURIs = new HashSet<URI>();
        hiddenAnnotationURIs.addAll(AnnotationPreferences.getHiddenAnnotationURIs());

        owlComponentFactory = new OWLComponentFactoryImpl(getOWLEditorKit());

        final OWLModelManager mngr = getOWLModelManager();

        owlModelManagerListener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                handleModelManagerEvent(event.getType());
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


        new OntologySourcesChangedHandlerUI(this);
    }


    private void handleOntologiesChanged(List<? extends OWLOntologyChange> changes) {
        for (OWLOntologyChange chg : changes){
            if (chg instanceof SetOntologyURI){
                rebuildOntologiesMenu();
                updateTitleBar();
                break;
            }
        }

        updateDirtyFlag();
    }


    private void updateDirtyFlag() {
        if (OSUtils.isOSX()){ // only currently implemented for OSX because it has a standard mechanism
            // set the modified indicator on the frame
            getRootPane().putClientProperty(WINDOW_MODIFIED, !getOWLModelManager().getDirtyOntologies().isEmpty());
        }
    }


    private void handleModelManagerEvent(EventType type) {
        if (reselectionEventTypes.contains(type)) {
            logger.debug("Reselection event..... verifying selections.");
            verifySelection();
        }

        if (type.equals(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
            updateTitleBar();
            rebuildList();
            rebuildOntologiesMenu();
            ontologiesList.repaint();
        }
        else if (type.equals(EventType.ONTOLOGY_CLASSIFIED)) {
            verifySelection();
        }
        else if (type.equals(EventType.ONTOLOGY_LOADED) || type.equals(EventType.ONTOLOGY_CREATED)) {
            if (getTabCount() > 0) {
                setSelectedTab(0);
            }
        }
        else if(type.equals(EventType.ENTITY_RENDERER_CHANGED)) {
            refreshComponents();
        }
        else if(type.equals(EventType.ONTOLOGY_SAVED)) {
            updateDirtyFlag();
        }
    }


    public void refreshComponents() {
        refreshComponents(this);
    }

    private void refreshComponents(Component component) {
        if(component instanceof Container) {
            Container cont = (Container) component;
            for(Component childComp : cont.getComponents()) {
                refreshComponents(childComp);
            }
        }
        if(component instanceof RefreshableComponent) {
            ((RefreshableComponent) component).refreshComponent();
        }
    }

    protected void verifySelection(Set<? extends OWLEntity> entities) {
        Set<OWLEntity> unreferencedEntities = new HashSet<OWLEntity>(entities);
        for (OWLEntity entity : entities) {
            if (entity != null) {
                for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
                    if (ont.containsEntityReference(entity)) {
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

        verifySelection(CollectionFactory.createSet(lastSelectedClass,
                                                    lastSelectedDataProperty,
                                                    lastSelectedObjectProperty,
                                                    lastSelectedAnnotationProperty,
                                                    lastSelectedIndividual,
                                                    lastSelectedDatatype,
                                                    selectedEntity));
    }


    public boolean isHiddenAnnotationURI(URI annotationURI) {
        return hiddenAnnotationURIs.contains(annotationURI);
    }


    public Set<URI> getHiddenAnnotationURIs() {
        return Collections.unmodifiableSet(hiddenAnnotationURIs);
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

        final OWLModelManager mngr = getOWLModelManager();

        ontologiesMenu = getOntologiesMenu(menuBar);
        rebuildOntologiesMenu();

        JMenu reasonerMenu = getReasonerMenu(menuBar);
        reasonerMenu.addSeparator();
        ButtonGroup bg = new ButtonGroup();
        Set<ProtegeOWLReasonerFactory> factories = mngr.getOWLReasonerManager().getInstalledReasonerFactories();
        List<ProtegeOWLReasonerFactory> factoriesList = new ArrayList<ProtegeOWLReasonerFactory>(factories);
        Collections.sort(factoriesList, new Comparator<ProtegeOWLReasonerFactory>() {
            public int compare(ProtegeOWLReasonerFactory o1, ProtegeOWLReasonerFactory o2) {
                return o1.getReasonerName().compareTo(o2.getReasonerName());
            }
        });
        for (final ProtegeOWLReasonerFactory plugin : factoriesList) {
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
        updateTitleBar();

        JMenu windowMenu = getWindowMenu(menuBar);
        windowMenu.addSeparator();
        windowMenu.add(new AbstractAction("Refresh User Interface") {

            public void actionPerformed(ActionEvent e) {
                refreshComponents();
            }
        });
    }


    private void rebuildOntologiesMenu() {
        if (ontologiesMenu != null){ // make sure the UI has been created before

            final OWLModelManager mngr = getOWLModelManager();

            ontologiesMenu.removeAll();
            ButtonGroup selButtons = new ButtonGroup();
            for (final OntologySelectionStrategy sel : mngr.getActiveOntologiesStrategies()){
                JRadioButtonMenuItem item = new JRadioButtonMenuItem(sel.getName());
                item.setSelected(mngr.getActiveOntologiesStrategy().equals(sel));
                item.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent event) {
                        mngr.setActiveOntologiesStrategy(sel);
                    }
                });
                selButtons.add(item);
                ontologiesMenu.add(item);

            }

            ontologiesMenu.addSeparator();
            ButtonGroup ontButtons = new ButtonGroup();
            Set<OWLOntology> orderedOntologies = new TreeSet<OWLOntology>(mngr.getOWLObjectComparator());
            orderedOntologies.addAll(mngr.getOntologies());
            for (final OWLOntology ont : orderedOntologies){
                JMenuItem item = new JRadioButtonMenuItem(mngr.getURIRendering(ont.getURI()));
                item.setToolTipText(ont.getURI().toString());
                item.setSelected(ont.equals(mngr.getActiveOntology()));
                item.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent event) {
                        mngr.setActiveOntology(ont);
                    }
                });
                ontButtons.add(item);
                ontologiesMenu.add(item);
            }
        }
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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 4, 0, 4);
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        topBarPanel.add(new OWLEntityNavPanel(getOWLEditorKit()), gbc);

        final OWLModelManager mngr = getOWLModelManager();

// Install the active ontology combo box
        ontologiesList = new JComboBox();
        ontologiesList.setToolTipText("Active ontology");
        ontologiesList.setRenderer(new OWLOntologyCellRenderer(getOWLEditorKit()));
        rebuildList();


        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 100; // Grow along the x axis
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        topBarPanel.add(ontologiesList, gbc);

        ontologiesList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OWLOntology ont = (OWLOntology) ontologiesList.getSelectedItem();
                if (ont != null) {
                    mngr.setActiveOntology(ont);
                }
            }
        });

// Global find field
        JPanel finderHolder = new JPanel();
        finderHolder.setLayout(new BoxLayout(finderHolder, BoxLayout.LINE_AXIS));
        finderHolder.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1,
                                                                                                  1,
                                                                                                  1,
                                                                                                  1,
                                                                                                  Color.LIGHT_GRAY),
                                                                  BorderFactory.createEmptyBorder(FINDER_BORDER, FINDER_BORDER,
                                                                                                  FINDER_BORDER, FINDER_BORDER)));
        final EntityFinderField entityFinderField = new EntityFinderField(this, getOWLEditorKit());
        final JLabel searchLabel = new JLabel(Icons.getIcon("object.search.gif"));
        final int height = entityFinderField.getPreferredSize().height;
        searchLabel.setPreferredSize(new Dimension(height, height));
        finderHolder.setMinimumSize(new Dimension(FINDER_MIN_WIDTH,
                                                  height+((FINDER_BORDER+1)*2)));
        finderHolder.add(searchLabel);
        finderHolder.add(entityFinderField);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        topBarPanel.add(finderHolder, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        topBarPanel.add(backgroundTaskLabel);
        topBarPanel.add(errorNotificationLabel);

        add(topBarPanel, BorderLayout.NORTH);

// Find focus accelerator
        KeyStroke findKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F,
                                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(findKeyStroke, "FOCUS_FIND");
        getActionMap().put("FOCUS_FIND", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                entityFinderField.requestFocus();
            }
        });
        updateTitleBar();
    }


    protected String getTitle() {
        final OWLModelManager mngr = getOWLModelManager();
        OWLOntology activeOntology = mngr.getActiveOntology();
        if (activeOntology == null) {
            return null;
        }
        URI locURI = mngr.getOntologyPhysicalURI(activeOntology);
        String location = "?";
        if (locURI != null) {
            location = locURI.toString();
            if (locURI.getScheme().equals("file")) {
                location = new File(locURI).getPath();
            }
        }

        String ontURI = mngr.getRendering(activeOntology);
        return ontURI + " - [" + location + "]";
    }


    private void updateTitleBar() {
        Frame f = ProtegeManager.getInstance().getFrame(this);
        if (f != null) {
            f.setTitle(getTitle());
        }
    }


    public void displayOWLEntity(OWLEntity owlEntity) {
        OWLEntityDisplayProvider candidate = null;
        for (OWLEntityDisplayProvider provider : entityDisplayProviders) {
            if (provider.canDisplay(owlEntity)) {
                if (candidate == null){
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


    protected CustomWorkspaceTabsManager getCustomTabsManager() {
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


    private void rebuildList() {
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


    public OWLEntityCreationSet<OWLNamedIndividual> createOWLIndividual() {
        return OWLEntityCreationPanel.showDialog(getOWLEditorKit(), "Please enter an individual name", OWLNamedIndividual.class);
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
                    return new File(getId() + "-config.xml").toURL();
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


            public WorkspaceTab newInstance() throws ClassNotFoundException, IllegalAccessException,
                    InstantiationException {
                return tab;
            }
        });

        return tab;
    }
}
