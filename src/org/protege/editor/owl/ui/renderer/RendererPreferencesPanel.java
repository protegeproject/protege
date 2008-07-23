package org.protege.editor.owl.ui.renderer;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.preferences.PreferencesPanelLayoutManager;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.protege.editor.owl.ui.prefix.PrefixMappingPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

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


    private JRadioButton uriFragmentRadioButton;

    private JRadioButton qnameRendererRadioButton;

    private JRadioButton annotationValueRadioButton;

    private JRadioButton annotationWithPrefixesRadioButton;

    private JList annotationPropertiesList;

    private JCheckBox highlightAOStatementsCheckBox;

    private JCheckBox showHyperlinksCheckBox;

    private JCheckBox highlightKeyWordsCheckBox;

    private JCheckBox useThatAsSynonymForAndCheckBox;

    private JSpinner fontSizeSpinner;

    private JComboBox fontCombo;

    private JButton showPrefixedsButton;

    private JButton showAnnotationsButton;


    public void applyChanges() {
        OWLRendererPreferences prefs = OWLRendererPreferences.getInstance();
        prefs.setRenderPrefixes(annotationWithPrefixesRadioButton.isSelected());
        if (annotationValueRadioButton.isSelected() ||
            annotationWithPrefixesRadioButton.isSelected()) {
            setupRenderer(new OWLEntityAnnotationValueRenderer());
            prefs.setRendererClass(OWLEntityAnnotationValueRenderer.class.getName());
        }
        else if (qnameRendererRadioButton.isSelected()) {
            setupRenderer(new OWLEntityQNameRenderer());
            prefs.setRendererClass(OWLEntityQNameRenderer.class.getName());
        }
        else {
            setupRenderer(new OWLEntityRendererImpl());
            prefs.setRendererClass(OWLEntityRendererImpl.class.getName());
        }
        prefs.setHighlightActiveOntologyStatements(highlightAOStatementsCheckBox.isSelected());
        prefs.setRenderHyperlinks(showHyperlinksCheckBox.isSelected());
        prefs.setHighlightKeyWords(highlightKeyWordsCheckBox.isSelected());
        prefs.setUseThatKeyword(useThatAsSynonymForAndCheckBox.isSelected());
        prefs.setFontSize((Integer) fontSizeSpinner.getValue());
        prefs.setFontName(fontCombo.getSelectedItem().toString());
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
        String clsName = prefs.getRendererClass();
        uriFragmentRadioButton = new JRadioButton("Render entities using URI fragment",
                                                  clsName.equals(OWLEntityRendererImpl.class.getName()));
        qnameRendererRadioButton = new JRadioButton("Render entities using qnames",
                                                    clsName.equals(OWLEntityQNameRenderer.class.getName()));
        annotationValueRadioButton = new JRadioButton("Render entities using annotation values",
                                                      clsName.equals(OWLEntityAnnotationValueRenderer.class.getName()) && !prefs.isRenderPrefixes());
        annotationWithPrefixesRadioButton = new JRadioButton("Render entities using annotation values with prefixes",
                                                      clsName.equals(OWLEntityAnnotationValueRenderer.class.getName()) && prefs.isRenderPrefixes());
        ButtonGroup bg = new ButtonGroup();
        bg.add(uriFragmentRadioButton);
        bg.add(qnameRendererRadioButton);
        bg.add(annotationValueRadioButton);
        bg.add(annotationWithPrefixesRadioButton);

        Box box = new Box(BoxLayout.Y_AXIS);
        box.setBorder(ComponentFactory.createTitledBorder("Entity rendering"));
        box.add(uriFragmentRadioButton);
        box.add(qnameRendererRadioButton);
        box.add(annotationValueRadioButton);
        box.add(annotationWithPrefixesRadioButton);
        box.add(Box.createVerticalStrut(4));

        JPanel optsPanel = new JPanel();
        optsPanel.setLayout(new BoxLayout(optsPanel, BoxLayout.LINE_AXIS));
        optsPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));
        optsPanel.setAlignmentX(0.0f);

        showPrefixedsButton = new JButton(new AbstractAction("Prefixes...") {
            public void actionPerformed(ActionEvent e) {
                PrefixMappingPanel.showDialog(getOWLEditorKit());
            }
        });
        optsPanel.add(showPrefixedsButton);

        showAnnotationsButton = new JButton(new AbstractAction("Annotations...") {
            public void actionPerformed(ActionEvent e) {
                AnnotationRendererPanel.showDialog(getOWLEditorKit());
            }
        });
        optsPanel.add(showAnnotationsButton);

        box.add(optsPanel);

        final ChangeListener l = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateRendererButtons();
            }
        };
        uriFragmentRadioButton.addChangeListener(l);
        qnameRendererRadioButton.addChangeListener(l);
        annotationValueRadioButton.addChangeListener(l);
        annotationWithPrefixesRadioButton.addChangeListener(l);

        updateRendererButtons();

        return box;
    }

    private void updateRendererButtons() {
        showPrefixedsButton.setEnabled(qnameRendererRadioButton.isSelected() ||
                                       annotationWithPrefixesRadioButton.isSelected());

        showAnnotationsButton.setEnabled(annotationValueRadioButton.isSelected() ||
                                         annotationWithPrefixesRadioButton.isSelected());
    }


    public void dispose() {
    }
}
