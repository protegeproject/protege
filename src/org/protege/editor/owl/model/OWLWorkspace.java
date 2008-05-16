package org.protege.editor.owl.model;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.RefreshableComponent;
import org.protege.editor.core.ui.error.ErrorLog;
import org.protege.editor.core.ui.error.ErrorNotificationLabel;
import org.protege.editor.core.ui.error.SendErrorReportHandler;
import org.protege.editor.core.ui.util.Icons;
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
import org.protege.editor.owl.ui.OWLEntityCreationPanel;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
import org.protege.editor.owl.ui.find.EntityFinderField;
import org.protege.editor.owl.ui.inference.ReasonerProgressUI;
import org.protege.editor.owl.ui.navigation.OWLEntityNavPanel;
import org.protege.editor.owl.ui.preferences.AnnotationPreferences;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.renderer.OWLIconProvider;
import org.protege.editor.owl.ui.renderer.OWLIconProviderImpl;
import org.protege.editor.owl.ui.renderer.OWLOntologyCellRenderer;
import org.protege.editor.owl.ui.selector.OWLClassSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLDataPropertySelectorPanel;
import org.protege.editor.owl.ui.selector.OWLIndividualSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.CollectionFactory;
import org.semanticweb.owl.util.OWLEntityCollectingOntologyChangeListener;
import org.semanticweb.owl.util.SimpleURIShortFormProvider;
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

    private JComboBox ontologiesList;

    private ArrayList<OWLEntityDisplayProvider> entityDisplayProviders;

    private OWLIconProvider iconProvider;

    private Map<String, Color> keyWordColorMap;

    private OWLSelectionModel owlSelectionModel;

    private OWLSelectionHistoryManager owlSelectionHistoryManager;

    private List<URI> defaultAnnotationProperties;

    private OWLModelManagerListener owlModelManagerListener;

    private OWLOntologyChangeListener ontologyChangeListener;

    private OWLOntologyChangeVisitor changeFilter;

    private Set<EventType> reselectionEventTypes = new HashSet<EventType>();

    private ErrorNotificationLabel errorNotificationLabel;

    private OWLClassSelectorPanel classSelectorPanel;

    private OWLObjectPropertySelectorPanel objectPropertySelectorPanel;

    private OWLDataPropertySelectorPanel dataPropertySelectorPanel;

    private OWLIndividualSelectorPanel individualSelectorPanel;

    private OWLEntityCollectingOntologyChangeListener listener;

    private Set<URI> hiddenAnnotationURIs;


    public OWLEditorKit getOWLEditorKit() {
        return (OWLEditorKit) getEditorKit();
    }


    public OWLModelManager getOWLModelManager() {
        return getOWLEditorKit().getOWLModelManager();
    }


    public void initialise() {
        entityDisplayProviders = new ArrayList<OWLEntityDisplayProvider>();
        iconProvider = new OWLIconProviderImpl(getOWLEditorKit().getOWLModelManager());
        owlSelectionModel = new OWLSelectionModelImpl();

        keyWordColorMap = new HashMap<String, Color>();
        Color restrictionColor = new Color(178, 0, 178);
        Color logicalOpColor = new Color(0, 178, 178);
        Color axiomColor = new Color(10, 94, 168);
        Color queryColor = new Color(100, 15, 120);
        keyWordColorMap.put("some", restrictionColor);
        keyWordColorMap.put("only", restrictionColor);
        keyWordColorMap.put("value", restrictionColor);
        keyWordColorMap.put("exactly", restrictionColor);
        keyWordColorMap.put("min", restrictionColor);
        keyWordColorMap.put("max", restrictionColor);
        keyWordColorMap.put("inv", logicalOpColor);
        keyWordColorMap.put("and", logicalOpColor);
        keyWordColorMap.put("that", logicalOpColor);
        keyWordColorMap.put("or", logicalOpColor);
        keyWordColorMap.put("not", logicalOpColor);
        keyWordColorMap.put("subClassOf", axiomColor);
        keyWordColorMap.put("SubClassOf", axiomColor);
        keyWordColorMap.put("disjointWith", axiomColor);
        keyWordColorMap.put("DisjointWith", axiomColor);
        keyWordColorMap.put("equivalentTo", axiomColor);
        keyWordColorMap.put("EquivalentTo", axiomColor);
        keyWordColorMap.put("domainOf", axiomColor);
        keyWordColorMap.put("DomainOf", axiomColor);
        keyWordColorMap.put("rangeOf", axiomColor);
        keyWordColorMap.put("RangeOf", axiomColor);
        keyWordColorMap.put("instanceOf", axiomColor);
        keyWordColorMap.put("InstanceOf", axiomColor);
        keyWordColorMap.put("minus", queryColor);
        keyWordColorMap.put("plus", queryColor);
        keyWordColorMap.put("possibly", queryColor);
        keyWordColorMap.put("inverseOf", axiomColor);
        keyWordColorMap.put("DifferentIndividuals:", axiomColor);
        keyWordColorMap.put("SameIndividuals:", axiomColor);
        keyWordColorMap.put("Functional:", axiomColor);
        keyWordColorMap.put("InverseFunctional:", axiomColor);
        keyWordColorMap.put("Symmetric:", axiomColor);
        keyWordColorMap.put("AntiSymmetric:", axiomColor);
        keyWordColorMap.put("Reflexive:", axiomColor);
        keyWordColorMap.put("Irreflexive:", axiomColor);
        keyWordColorMap.put("Transitive:", axiomColor);
        keyWordColorMap.put("subPropertyOf", axiomColor);
        keyWordColorMap.put("disjointUnionOf", axiomColor);
        keyWordColorMap.put("o", axiomColor);
        keyWordColorMap.put("\u279E", axiomColor);
        keyWordColorMap.put("\u2192", axiomColor);
        keyWordColorMap.put("\u2227", axiomColor);

        defaultAnnotationProperties = new ArrayList<URI>();
        defaultAnnotationProperties.add(OWLRDFVocabulary.RDFS_COMMENT.getURI());
        super.initialise();

        errorNotificationLabel = new ErrorNotificationLabel(ProtegeApplication.getErrorLog(), this);


        createActiveOntologyPanel();
        reselectionEventTypes.add(EventType.ACTIVE_ONTOLOGY_CHANGED);
        reselectionEventTypes.add(EventType.ENTITY_RENDERER_CHANGED);
        reselectionEventTypes.add(EventType.ENTITY_RENDERING_CHANGED);
        reselectionEventTypes.add(EventType.ONTOLOGY_VISIBILITY_CHANGED);
        reselectionEventTypes.add(EventType.REASONER_CHANGED);

        hiddenAnnotationURIs = new HashSet<URI>();
        hiddenAnnotationURIs.addAll(AnnotationPreferences.getHiddenAnnotationURIs());

        owlModelManagerListener = new OWLModelManagerListener() {

            public void handleChange(OWLModelManagerChangeEvent event) {
                if (reselectionEventTypes.contains(event.getType())) {
                    logger.debug("Reselection event..... verifying selections.");
                    verifySelection();
                }

                if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                    updateTitleBar();
                    rebuildList();
                    ontologiesList.repaint();
                }

                if (event.isType(EventType.ONTOLOGY_CLASSIFIED)) {
                    verifySelection();
                }

                if (event.isType(EventType.ONTOLOGY_LOADED) || event.isType(EventType.ONTOLOGY_CREATED)) {
                    if (getTabCount() > 0) {
                        setSelectedTab(0);
                    }
                }

                if(event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
                    refreshComponents();
                }
            }
        };
        getOWLModelManager().addListener(owlModelManagerListener);
        listener = new OWLEntityCollectingOntologyChangeListener() {
            public void ontologiesChanged() {
                verifySelection(getEntities());
            }
        };
        getOWLModelManager().addOntologyChangeListener(listener);
        getOWLModelManager().getOWLReasonerManager().setReasonerProgressMonitor(new ReasonerProgressUI(getOWLEditorKit()));
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
        OWLIndividual lastSelectedIndividual = selectionModel.getLastSelectedIndividual();
        OWLEntity selectedEntity = selectionModel.getSelectedEntity();

        selectionModel.setSelectedEntity(lastSelectedClass);
        selectionModel.setSelectedEntity(lastSelectedObjectProperty);
        selectionModel.setSelectedEntity(lastSelectedDataProperty);
        selectionModel.setSelectedEntity(lastSelectedIndividual);
        selectionModel.setSelectedEntity(selectedEntity);

        verifySelection(CollectionFactory.createSet(lastSelectedClass,
                lastSelectedDataProperty,
                lastSelectedIndividual,
                lastSelectedObjectProperty,
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
            getOWLEditorKit().getOWLModelManager().fireEvent(EventType.ACTIVE_ONTOLOGY_CHANGED);
        }
    }


    public void setHiddenAnnotationURIs(Set<URI> hiddenURIs) {
        if (!hiddenURIs.equals(hiddenAnnotationURIs)) {
            hiddenAnnotationURIs.clear();
            hiddenAnnotationURIs.addAll(hiddenURIs);
            AnnotationPreferences.setHiddenAnnotationURIs(hiddenAnnotationURIs);
            getOWLEditorKit().getOWLModelManager().fireEvent(EventType.ACTIVE_ONTOLOGY_CHANGED);
        }
    }


    protected void initialiseExtraMenuItems(JMenuBar menuBar) {
        super.initialiseExtraMenuItems(menuBar);
        JMenu reasonerMenu = getReasonerMenu(menuBar);
        reasonerMenu.addSeparator();
        ButtonGroup bg = new ButtonGroup();
        Set<ProtegeOWLReasonerFactory> factories = getOWLModelManager().getOWLReasonerManager().getInstalledReasonerFactories();
        List<ProtegeOWLReasonerFactory> factoriesList = new ArrayList<ProtegeOWLReasonerFactory>(factories);
        Collections.sort(factoriesList, new Comparator<ProtegeOWLReasonerFactory>() {
            public int compare(ProtegeOWLReasonerFactory o1, ProtegeOWLReasonerFactory o2) {
                return o1.getReasonerName().compareTo(o2.getReasonerName());
            }
        });
        for (final ProtegeOWLReasonerFactory plugin : factoriesList) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(plugin.getReasonerName());
            item.setSelected(getOWLModelManager().getOWLReasonerManager().getCurrentReasonerFactoryId().equals(plugin.getReasonerId()));
            reasonerMenu.add(item);
            bg.add(item);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getOWLModelManager().getOWLReasonerManager().setCurrentReasonerFactoryId(plugin.getReasonerId());
                }
            });
        }
        updateTitleBar();
        JMenu windowMenu = getWindowMenu(menuBar);
        windowMenu.addSeparator();
        if(windowMenu != null) {
            windowMenu.add(new AbstractAction("Refresh User Interface") {

                public void actionPerformed(ActionEvent e) {
                    refreshComponents();
                }
            });
        }
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
        rebuildList();
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
        finderHolder.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1,
                1,
                1,
                1,
                Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        final EntityFinderField entityFinderField = new EntityFinderField(this, getOWLEditorKit());
        finderHolder.add(new JLabel(Icons.getIcon("object.search.gif")));
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
        OWLOntology activeOntology = getOWLModelManager().getActiveOntology();
        if (activeOntology == null) {
            return null;
        }
        URI locURI = getOWLModelManager().getOntologyPhysicalURI(activeOntology);
        String location = "?";
        if (locURI != null) {
            location = locURI.toString();
            if (locURI.getScheme().equals("file")) {
                location = new File(locURI).getPath();
            }
        }

        String ontURI = activeOntology.getURI().toString();
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
                candidate = provider;
                if (provider.getDisplayComponent().isShowing()) {
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
        if (classSelectorPanel != null) {
            classSelectorPanel.dispose();
        }
        if (objectPropertySelectorPanel != null) {
            objectPropertySelectorPanel.dispose();
        }
        if (dataPropertySelectorPanel != null) {
            dataPropertySelectorPanel.dispose();
        }
        if (individualSelectorPanel != null) {
            individualSelectorPanel.dispose();
        }
        getOWLModelManager().removeListener(owlModelManagerListener);
        getOWLModelManager().removeOntologyChangeListener(listener);
    }


    private void rebuildList() {
        try {
            TreeSet<OWLOntology> ts = new TreeSet<OWLOntology>(new Comparator<OWLOntology>() {

                SimpleURIShortFormProvider sfp = new SimpleURIShortFormProvider();
                public int compare(OWLOntology o1, OWLOntology o2) {
                    return sfp.getShortForm(o1.getURI()).compareToIgnoreCase(sfp.getShortForm(o2.getURI()));
                }
            });
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
        return createOWLCellRenderer(renderExpression, renderIcon, 0);
    }


    public OWLCellRenderer createOWLCellRenderer(boolean renderExpression, boolean renderIcon, int indentation) {
        return new OWLCellRenderer(getOWLEditorKit(), renderExpression, renderIcon, indentation);
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


    private OWLEntityCreationPanel.URIShortNamePair showDialog(String message, int type) {
        return OWLEntityCreationPanel.showDialog(getOWLEditorKit(), message, type);
    }


    public OWLEntityCreationSet<OWLClass> createOWLClass() {
        OWLEntityCreationPanel.URIShortNamePair pair = showDialog("Please enter a class name",
                OWLEntityCreationPanel.TYPE_CLASS);
        if (pair == null) {
            return null;
        }
        return getOWLModelManager().getOWLEntityFactory().createOWLClass(pair.getShortName(), pair.getUri());
    }


    public OWLEntityCreationSet<OWLObjectProperty> createOWLObjectProperty() {
        OWLEntityCreationPanel.URIShortNamePair pair = showDialog("Please enter an object property name",
                OWLEntityCreationPanel.TYPE_OBJECT_PROPERTY);
        if (pair == null) {
            return null;
        }
        return getOWLModelManager().getOWLEntityFactory().createOWLObjectProperty(pair.getShortName(), pair.getUri());
    }


    public OWLEntityCreationSet<OWLDataProperty> createOWLDataProperty() {
        OWLEntityCreationPanel.URIShortNamePair pair = showDialog("Please enter a data property name",
                OWLEntityCreationPanel.TYPE_DATA_PROPERTY);
        if (pair == null) {
            return null;
        }
        return getOWLModelManager().getOWLEntityFactory().createOWLDataProperty(pair.getShortName(), pair.getUri());
    }


    public OWLEntityCreationSet<OWLIndividual> createOWLIndividual() {
        OWLEntityCreationPanel.URIShortNamePair pair = showDialog("Please enter an individual name",
                OWLEntityCreationPanel.TYPE_INDIVIDUAL);
        if (pair == null) {
            return null;
        }
        return getOWLModelManager().getOWLEntityFactory().createOWLIndividual(pair.getShortName(), pair.getUri());
    }


    public OWLClassSelectorPanel getOWLClassSelectorPanel() {
        if (classSelectorPanel == null) {
            classSelectorPanel = new OWLClassSelectorPanel(getOWLEditorKit());
        }
        return classSelectorPanel;
    }


    public OWLObjectPropertySelectorPanel getOWLObjectPropertySelectorPanel() {
        if (objectPropertySelectorPanel == null) {
            objectPropertySelectorPanel = new OWLObjectPropertySelectorPanel(getOWLEditorKit());
        }
        return objectPropertySelectorPanel;
    }


    public OWLDataPropertySelectorPanel getOWLDataPropertySelectorPanel() {
        if (dataPropertySelectorPanel == null) {
            dataPropertySelectorPanel = new OWLDataPropertySelectorPanel(getOWLEditorKit());
        }
        return dataPropertySelectorPanel;
    }


    public OWLIndividualSelectorPanel getOWLIndividualSelectorPanel() {
        if (individualSelectorPanel == null) {
            individualSelectorPanel = new OWLIndividualSelectorPanel(getOWLEditorKit());
        }
        return individualSelectorPanel;
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
