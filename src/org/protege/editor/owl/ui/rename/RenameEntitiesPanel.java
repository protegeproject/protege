package org.protege.editor.owl.ui.rename;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.CheckTable;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.find.EntityFinderPreferences;
import org.protege.editor.owl.ui.OWLObjectComparator;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLEntityRendererImpl;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLObjectDuplicator;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 1, 2008<br><br>
 */
public class RenameEntitiesPanel extends JPanel implements VerifiedInputEditor {

    private Logger logger = Logger.getLogger(RenameEntitiesPanel.class);

    // editor pause
    private static final int SEARCH_PAUSE_MILLIS = 1000;

    private OWLEditorKit eKit;

    private Map<String, Set<OWLEntity>> nsMap = new HashMap<String, Set<OWLEntity>>();

    private JComboBox replaceWithCombo;

    private JComboBox findCombo;

    private CheckTable<OWLEntity> list;

    private OWLEntityRenderer fragRenderer;

    private ItemListener findListener = new ItemListener(){
        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED){
                reloadEntityListThreaded();
            }
        }
    };

    private ItemListener replaceListener = new ItemListener(){
        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED){
                handleStateChanged();
            }
        }
    };

    private ListSelectionListener listSelListener = new ListSelectionListener(){
        public void valueChanged(ListSelectionEvent event) {
            handleStateChanged();
        }
    };


    public RenameEntitiesPanel(OWLEditorKit eKit) {
        setLayout(new BorderLayout());
        this.eKit = eKit;

        refreshMap();

        JComponent subPanel = new JPanel();
        subPanel.setBorder(new TitledBorder("Find & Replace"));
        subPanel.setLayout(new BorderLayout());

        findCombo = createCombo("Find", findListener, subPanel, BorderLayout.NORTH);

        replaceWithCombo = createCombo("Replace with", replaceListener, subPanel, BorderLayout.SOUTH);

        add(subPanel, BorderLayout.NORTH);

        list = new CheckTable<OWLEntity>("Matching entities");
        list.checkAll(true);
        list.setDefaultRenderer(new ResultCellRenderer(eKit));
        list.addCheckSelectionListener(listSelListener);

        add(new JScrollPane(list), BorderLayout.CENTER);
    }


    private JComboBox createCombo(String title, final ItemListener listener, JComponent parent, String constraints) {
        final JComboBox combo = new JComboBox(nsMap.keySet().toArray());
        combo.addItem("");
        combo.setSelectedItem("");
        combo.setEditable(true);
        combo.addItemListener(listener);

        final JTextComponent editor = (JTextComponent) combo.getEditor().getEditorComponent();

        final ActionListener actionListener = new ActionListener(){
               public void actionPerformed(ActionEvent actionEvent) {
                   try {
                       combo.setSelectedItem(editor.getText());
                   }
                   catch (Exception e) {
                       e.printStackTrace();
                   }
               }
           };

        final Timer timer = new Timer(SEARCH_PAUSE_MILLIS, actionListener);

        editor.getDocument().addDocumentListener(new DocumentListener(){
            private void handleUpdate() {
                timer.restart();
            }

            public void insertUpdate(DocumentEvent event) {
                handleUpdate();
            }

            public void removeUpdate(DocumentEvent event) {
                handleUpdate();
            }

            public void changedUpdate(DocumentEvent event) {
            }
        });
        JComponent panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(new JLabel(title));
        panel.add(combo);
        parent.add(panel, constraints);
        return combo;
    }


    public String getFindValue(){
        return (String) findCombo.getSelectedItem();
    }


    public String getReplaceWithValue(){
        return (String) replaceWithCombo.getSelectedItem();
    }


    public List<OWLEntity> getSelectedEntities(){
        return list.getFilteredValues();
    }


    public List<OWLOntologyChange> performRename(Collection<OWLEntity> entities, String pattern, String newText){
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        // cannot use an OWLEntityRenamer directly because multiple entities may be referenced by the same axiom

        Map<OWLEntity, URI> uriMap = generateNameMap(entities, pattern, newText);

        // perform the rename across all loaded ontologies
        for(OWLOntology ont : eKit.getOWLModelManager().getOntologies()) {
            Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
            for (OWLEntity entity : entities){
                axioms.addAll(getAxioms(ont, entity));
            }

            OWLObjectDuplicator duplicator = new OWLObjectDuplicator(uriMap, eKit.getOWLModelManager().getOWLDataFactory());
            fillListWithTransformChanges(changes, axioms, ont, duplicator);
        }

        return changes;
    }


    private void reloadEntityList() {
        final ArrayList<OWLEntity> sortedEntities = new ArrayList<OWLEntity>(getEntities());
        Collections.sort(sortedEntities, new OWLObjectComparator<OWLEntity>(eKit.getOWLModelManager()));
        list.setData(sortedEntities);
        handleStateChanged();
    }


    private Set<OWLEntity> getEntities(){
        Set<OWLEntity> matches = nsMap.get(getFindValue());
        if (matches == null){
            EntityFinderPreferences prefs = EntityFinderPreferences.getInstance();
            String prefix = prefs.isUseRegularExpressions() ? ".*" : "*";
            matches = eKit.getOWLModelManager().getEntityFinder().getEntities(prefix + getFindValue());
        }
        return matches;
    }


    private void refreshMap() {
        for (OWLOntology ont : eKit.getOWLModelManager().getActiveOntologies()){
            for (OWLEntity entity : ont.getReferencedEntities()){
                extractNSFromEntity(entity);
            }
        }
    }


    private void extractNSFromEntity(OWLEntity entity) {
        if (fragRenderer == null){
            fragRenderer = new OWLEntityRendererImpl();
        }
        String frag = fragRenderer.render(entity);
        final String uriStr = entity.getURI().toString();
        String ns = uriStr.substring(0, uriStr.lastIndexOf(frag));
        Set<OWLEntity> matchingEntities = nsMap.get(ns);
        if (matchingEntities == null){
            matchingEntities = new HashSet<OWLEntity>();
        }
        matchingEntities.add(entity);
        nsMap.put(ns, matchingEntities);
    }


    private Map<OWLEntity, URI> generateNameMap(Collection<OWLEntity> entities, String pattern, String newText) {
        Map<OWLEntity, URI> uriMap = new HashMap<OWLEntity, URI>();

        for (OWLEntity entity : entities){
            String newURIStr = entity.getURI().toString().replaceAll(pattern, newText);
            try {
                URI newURI = new URI(newURIStr);
                uriMap.put(entity, newURI);
            }
            catch (URISyntaxException e) {
                logger.warn("Could not rename entity (" + eKit.getOWLModelManager().getRendering(entity) + "). " + newURIStr + " not a valid URI.");
            }
        }
        return uriMap;
    }


    private static Set<OWLAxiom> getAxioms(OWLOntology ont, OWLEntity entity) {
        return ont.getReferencingAxioms(entity);
    }


    private static void fillListWithTransformChanges(List<OWLOntologyChange> changes,
                                                     Set<OWLAxiom> axioms, OWLOntology ont,
                                                     OWLObjectDuplicator duplicator) {
        for(OWLAxiom ax : axioms) {
            changes.add(new RemoveAxiom(ont, ax));
            OWLAxiom dupAx = duplicator.duplicateObject(ax);
            changes.add(new AddAxiom(ont, dupAx));
        }
    }


/////////////////////// validation

    private List<InputVerificationStatusChangedListener> statusListeners = new ArrayList<InputVerificationStatusChangedListener>();

    private boolean currentStatus = false;

    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        statusListeners.add(listener);
        listener.verifiedStatusChanged(currentStatus);
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        statusListeners.remove(listener);
    }


    private void handleStateChanged() {
        boolean valid = getStatus();
        if (currentStatus != valid){
            currentStatus = valid;
            for (InputVerificationStatusChangedListener l : statusListeners){
                l.verifiedStatusChanged(currentStatus);
            }
        }
    }


    private boolean getStatus() {
        return findCombo.getSelectedItem() != null &&!findCombo.getSelectedItem().equals("") &&
                replaceWithCombo.getSelectedItem() != null && !replaceWithCombo.getSelectedItem().equals("") &&
                !list.getFilteredValues().isEmpty();
    }


//////////////////// reload threading

    private Thread reloadThread;

    private Runnable reloadProcess = new Runnable(){
        public void run() {
            reloadEntityList();
            reloadThread = null;
        }
    };


    private void reloadEntityListThreaded(){
        if (reloadThread != null && reloadThread.isAlive()){
            System.out.println("interrupting current reload");
            reloadThread.interrupt();
        }

        reloadThread = new Thread(reloadProcess);

        reloadThread.run();
    }


    public JComponent getFocusComponent() {
        return (JComponent)findCombo.getEditor().getEditorComponent();
    }


    /**
     * Should highlight the matching text in the URI
     */
    class ResultCellRenderer extends OWLCellRenderer {

        public Style fadedStyle;


        public ResultCellRenderer(OWLEditorKit owlEditorKit) {
            super(owlEditorKit);
        }


        protected String getRendering(Object object) {
            if (object instanceof OWLEntity){
                return super.getRendering(object) + " (" + ((OWLEntity)object).getURI() + ")";
            }
            return super.getRendering(object);
        }


        protected void renderToken(String curToken, int tokenStartIndex, StyledDocument doc) {
            super.renderToken(curToken, tokenStartIndex, doc);
//                System.out.println("curToken = " + curToken);
//                System.out.println("tokenStartIndex = " + tokenStartIndex);
//                System.out.println("doc.getLength() = " + doc.getLength());
//                if (curToken.startsWith("(")){
//                    if (fadedStyle == null){
//                        fadedStyle = doc.addStyle("FADED_STYLE", null);
//                        StyleConstants.setForeground(fadedStyle, Color.GRAY);
//                    }
//                    doc.setCharacterAttributes(tokenStartIndex, doc.getLength(), fadedStyle, true);
//                }
        }
    }
}
