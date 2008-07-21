package org.protege.editor.owl.ui.rename;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.CheckTable;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.find.EntityFinderPreferences;
import org.protege.editor.owl.model.refactor.EntityFindAndReplaceURIRenamer;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLEntityRendererImpl;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;

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

    private Set<OWLEntity> errors = Collections.emptySet();

    private EntityFindAndReplaceURIRenamer renamer;

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
                updateErrors();
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
        setLayout(new BorderLayout(6, 6));
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
                combo.setSelectedItem(editor.getText());
            }
        };

        final Timer timer = new Timer(SEARCH_PAUSE_MILLIS, actionListener);

        editor.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent event) {
                timer.restart();
            }

            public void removeUpdate(DocumentEvent event) {
                timer.restart();
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


    public EntityFindAndReplaceURIRenamer getRenamer(){
        return renamer;
    }


    private void reloadEntityList() {
        final ArrayList<OWLEntity> sortedEntities = new ArrayList<OWLEntity>(getEntities());
        Collections.sort(sortedEntities, eKit.getOWLModelManager().getOWLObjectComparator());
        list.setData(sortedEntities);
        updateErrors();
        handleStateChanged();
    }


    private Set<OWLEntity> getEntities(){
        Set<OWLEntity> matches = nsMap.get(getFindValue());
        if (matches == null){
            matches = new HashSet<OWLEntity>();
            Set<OWLEntity> ents = new HashSet<OWLEntity>();
            for (OWLOntology ont : getOntologies()){
                ents.addAll(ont.getReferencedEntities());
            }
            EntityFinderPreferences prefs = EntityFinderPreferences.getInstance();
            String matchingVal = getFindValue();
            if (!prefs.isUseRegularExpressions()){
                matchingVal = "(?i).*" + matchingVal + ".*";
            }
            Pattern p = Pattern.compile(matchingVal);
            for (OWLEntity ent : ents){
                if (p.matcher(ent.getURI().toString()).matches()){
                    matches.add(ent);
                }
            }
//            matches = eKit.getOWLModelManager().getEntityFinder().getEntities(prefix + getFindValue());
        }
        return matches;
    }


    private Set<OWLOntology> getOntologies() {
        return eKit.getOWLModelManager().getOntologies();
    }


    private void updateErrors() {
        final OWLModelManager mngr = eKit.getOWLModelManager();
        renamer = new EntityFindAndReplaceURIRenamer(mngr.getOWLOntologyManager(),
                                                     list.getAllValues(),
                                                     getOntologies(),
                                                     getFindValue(), getReplaceWithValue());
        errors = renamer.getErrors().keySet();
        list.repaint();
    }


    private void refreshMap() {
        for (OWLOntology ont : getOntologies()){
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
                !list.getFilteredValues().isEmpty() && errors.isEmpty();
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

        private boolean inURI = false;

        private Style fadedStyle = null;

        private Style highlightedStyle = null;


        public ResultCellRenderer(OWLEditorKit owlEditorKit) {
            super(owlEditorKit);
        }


        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (errors.contains(value)){
                setStrikeThrough(true);
            }
            else{
                setStrikeThrough(false);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }


        protected String getRendering(Object object) {
            if (object instanceof OWLEntity){
                return super.getRendering(object) + " (" + ((OWLEntity)object).getURI() + ")";
            }
            return super.getRendering(object);
        }


        protected void renderToken(String curToken, int tokenStartIndex, StyledDocument doc) {
            super.renderToken(curToken, tokenStartIndex, doc);
            if (logger.isDebugEnabled()) {
                logger.debug("curToken = " + curToken);
            }
            if (curToken.startsWith("(")){
                inURI = true;
            }
            if (curToken.equals(")")){
                inURI = false;
            }
            else if(inURI){
                if (fadedStyle == null){
                    fadedStyle = doc.addStyle("MY_FADED_STYLE", null);
                    StyleConstants.setForeground(fadedStyle, Color.GRAY);
                }
                doc.setCharacterAttributes(tokenStartIndex, doc.getLength()-tokenStartIndex, fadedStyle, false);

                if (highlightedStyle == null){
                    highlightedStyle = doc.addStyle("MY_HIGHLIGHTED_STYLE", null);
                    StyleConstants.setForeground(highlightedStyle, Color.darkGray);
                    StyleConstants.setBold(highlightedStyle, true);
                }
                final String s = getFindValue().toLowerCase();
                curToken = curToken.toLowerCase();
                int cur = 0;
                do {
                    cur = curToken.indexOf(s, cur);
                    if (cur != -1){
                        doc.setCharacterAttributes(tokenStartIndex + cur,  s.length(), highlightedStyle, true);
                        cur++;
                    }
                }
                while (cur != -1);

            }
        }
    }
}
