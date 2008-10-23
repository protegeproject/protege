package org.protege.editor.owl.ui.renderer;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.preferences.PreferencesPanelLayoutManager;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.protege.editor.owl.ui.prefix.PrefixMappingPanel;
import org.semanticweb.owl.model.OWLRuntimeException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RendererPreferencesPanel extends OWLPreferencesPanel {

    private static final Logger logger = Logger.getLogger(RendererPreferencesPanel.class);

    private Map<JRadioButton, Class<? extends OWLModelManagerEntityRenderer>> buttonToRendererMap = new HashMap<JRadioButton, Class<? extends OWLModelManagerEntityRenderer>>();

    private Map<Class<? extends OWLModelManagerEntityRenderer>, Boolean> rendererToPrefixesMap = new HashMap<Class<? extends OWLModelManagerEntityRenderer>, Boolean>();

    private Map<Class<? extends OWLModelManagerEntityRenderer>, Boolean> rendererToAnnotationsMap = new HashMap<Class<? extends OWLModelManagerEntityRenderer>, Boolean>();

    private JList annotationPropertiesList;

    private JCheckBox highlightAOStatementsCheckBox;

    private JCheckBox showHyperlinksCheckBox;

    private JCheckBox highlightKeyWordsCheckBox;

    private JCheckBox useThatAsSynonymForAndCheckBox;

    private JSpinner fontSizeSpinner;

    private JComboBox fontCombo;

    private JButton showPrefixedsButton;

    private JButton showAnnotationsButton;

    private String originalClassName;

    private boolean dirty = false;


    public void applyChanges() {
        OWLRendererPreferences prefs = OWLRendererPreferences.getInstance();
        prefs.setHighlightActiveOntologyStatements(highlightAOStatementsCheckBox.isSelected());
        prefs.setRenderHyperlinks(showHyperlinksCheckBox.isSelected());
        prefs.setHighlightKeyWords(highlightKeyWordsCheckBox.isSelected());
        prefs.setUseThatKeyword(useThatAsSynonymForAndCheckBox.isSelected());
        prefs.setFontSize((Integer) fontSizeSpinner.getValue());
        prefs.setFontName(fontCombo.getSelectedItem().toString());

        if (isDirty()){
            Class<? extends OWLModelManagerEntityRenderer> cls = getSelectedRendererClass();
            try {
                prefs.setRendererClass(cls.getName());
                OWLModelManagerEntityRenderer ren = cls.newInstance();
                setupRenderer(ren);
            }
            catch (Exception e) {
                throw new OWLRuntimeException(e);
            }
        }
        getOWLEditorKit().getWorkspace().refreshComponents();
    }


    private void setupRenderer(OWLModelManagerEntityRenderer renderer) {
        renderer.setup(getOWLModelManager());
        renderer.initialise();
        getOWLModelManager().setOWLEntityRenderer(renderer);
    }


    public void initialise() throws Exception {
        setLayout(new BorderLayout());

        OWLRendererPreferences prefs = OWLRendererPreferences.getInstance();

        Box holderBox = new Box(BoxLayout.Y_AXIS);
        add(holderBox, BorderLayout.NORTH);

        holderBox.add(createRendererSelectionPanel());

        highlightAOStatementsCheckBox = new JCheckBox("Highlight active ontology statements",
                                                      prefs.isHighlightActiveOntologyStatements());
        showHyperlinksCheckBox = new JCheckBox("Show hyperlinks in components that support them",
                                               prefs.isRenderHyperlinks());
        highlightKeyWordsCheckBox = new JCheckBox("Highlight keywords", prefs.isHighlightKeyWords());

        useThatAsSynonymForAndCheckBox = new JCheckBox("Use the 'that' keyword as a synonym for the 'and' keyword",
                                                       prefs.isUseThatKeyword());

        Box optBox = new Box(BoxLayout.Y_AXIS);
        optBox.setAlignmentX(0.0f);
        optBox.setBorder(ComponentFactory.createTitledBorder("Appearance"));
        optBox.add(highlightAOStatementsCheckBox);
        optBox.add(showHyperlinksCheckBox);
        optBox.add(highlightKeyWordsCheckBox);
        optBox.add(useThatAsSynonymForAndCheckBox);
        optBox.add(Box.createVerticalStrut(4));
        holderBox.add(optBox);


        JPanel fontSizePanel = new JPanel();
        PreferencesPanelLayoutManager man = new PreferencesPanelLayoutManager(fontSizePanel);
        fontSizePanel.setLayout(man);
        fontSizePanel.setBorder(ComponentFactory.createTitledBorder("Font"));
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(prefs.getFontSize(), 1, 120, 1));
        fontSizePanel.add("Font size", fontSizeSpinner);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font [] fonts = ge.getAllFonts();
        ArrayList<String> fontNames = new ArrayList<String>();
        for(Font f : fonts) {
            fontNames.add(f.getName());
        }
        fontCombo = new JComboBox(fontNames.toArray());
        fontSizePanel.add("Font", fontCombo);
        fontCombo.setSelectedItem(prefs.getFontName());

        holderBox.add(fontSizePanel);
    }

    private Component createRendererSelectionPanel() {
        OWLRendererPreferences prefs = OWLRendererPreferences.getInstance();
        originalClassName = prefs.getRendererClass();

        addRenderer("Render entities using URI fragment", OWLEntityRendererImpl.class, false, false);
        addRenderer("Render entities using qnames", OWLEntityQNameRenderer.class, true, false);
        addRenderer("Render entities using annotation values", OWLEntityAnnotationValueRenderer.class, false, true);
        addRenderer("Render entities using annotation values with prefixes", PrefixedOWLEntityAnnotationValueRenderer.class, true, true);


        Box box = new Box(BoxLayout.Y_AXIS);
        box.setBorder(ComponentFactory.createTitledBorder("Entity rendering"));

        ButtonGroup bg = new ButtonGroup();
        for (JRadioButton button : buttonToRendererMap.keySet()){
            bg.add(button);
            box.add(button);
        }

        box.add(Box.createVerticalStrut(4));

        JPanel optsPanel = new JPanel();
        optsPanel.setLayout(new BoxLayout(optsPanel, BoxLayout.LINE_AXIS));
        optsPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));
        optsPanel.setAlignmentX(0.0f);

        showPrefixedsButton = new JButton(new AbstractAction("Prefixes...") {
            public void actionPerformed(ActionEvent e) {
                if (PrefixMappingPanel.showDialog(getOWLEditorKit())){
                    dirty = true;
                }
            }
        });
        optsPanel.add(showPrefixedsButton);

        showAnnotationsButton = new JButton(new AbstractAction("Annotations...") {
            public void actionPerformed(ActionEvent e) {
                if (AnnotationRendererPanel.showDialog(getOWLEditorKit())){
                    dirty = true;
                }
            }
        });
        optsPanel.add(showAnnotationsButton);

        box.add(optsPanel);

        final ChangeListener l = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateRendererButtons();
            }
        };

        for (JRadioButton button : buttonToRendererMap.keySet()){
            button.addChangeListener(l);
        }

        updateRendererButtons();

        return box;
    }


    private void addRenderer(String label, Class<? extends OWLModelManagerEntityRenderer> rendererClass, boolean showPrefixOptions, boolean showAnnotationOptions) {
        String currentRendererClassName = OWLRendererPreferences.getInstance().getRendererClass();
        JRadioButton button = new JRadioButton(label, currentRendererClassName.equals(rendererClass.getName()));
        buttonToRendererMap.put(button, rendererClass);
        rendererToPrefixesMap.put(rendererClass, showPrefixOptions);
        rendererToAnnotationsMap.put(rendererClass, showAnnotationOptions);
    }


    private void updateRendererButtons() {
        final Class<? extends OWLEntityRenderer> cls = getSelectedRendererClass();
        if (cls != null){
            showPrefixedsButton.setEnabled(rendererToPrefixesMap.get(cls));
            showAnnotationsButton.setEnabled(rendererToAnnotationsMap.get(cls));
        }
    }


    public void dispose() {
    }


    public boolean isDirty() {
        return dirty || (getSelectedRendererClass() != null && !getSelectedRendererClass().getName().equals(originalClassName));
    }


    public Class<? extends OWLModelManagerEntityRenderer> getSelectedRendererClass() {
        for (JRadioButton button : buttonToRendererMap.keySet()){
            if (button.isSelected()){
                return buttonToRendererMap.get(button);
            }
        }
        return null;
    }
}
