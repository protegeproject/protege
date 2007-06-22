package org.protege.editor.owl.ui.renderer;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.protege.editor.owl.ui.prefix.PrefixMappingPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;

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

    private JList annotationPropertiesList;


    private JCheckBox highlightAOStatementsCheckBox;

    private JCheckBox showHyperlinksCheckBox;

    private JCheckBox showChangedEntitiesCheckBox;

    private JCheckBox highlightKeyWordsCheckBox;


    public void applyChanges() {
        OWLRendererPreferences prefs = OWLRendererPreferences.getInstance();
        if (annotationValueRadioButton.isSelected()) {
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
        prefs.setHighlightChangedEntities(showChangedEntitiesCheckBox.isSelected());
        prefs.setHighlightKeyWords(highlightKeyWordsCheckBox.isSelected());
    }


    private void setupRenderer(OWLModelManagerEntityRenderer renderer) {
        renderer.setup(getOWLModelManager());
        renderer.initialise();
        getOWLModelManager().setOWLEntityRenderer(renderer);
    }


    public void initialise() throws Exception {
        setLayout(new BorderLayout());

        Box holderBox = new Box(BoxLayout.Y_AXIS);
        add(holderBox, BorderLayout.NORTH);
        OWLRendererPreferences prefs = OWLRendererPreferences.getInstance();
        String clsName = prefs.getRendererClass();
        uriFragmentRadioButton = new JRadioButton("Render entities using URI fragment",
                                                  clsName.equals(OWLEntityRendererImpl.class.getName()));
        qnameRendererRadioButton = new JRadioButton("Render entities using qnames",
                                                    clsName.equals(OWLEntityQNameRenderer.class.getName()));
        annotationValueRadioButton = new JRadioButton("Render entities using annotation values",
                                                      clsName.equals(OWLEntityAnnotationValueRenderer.class.getName()));

        ButtonGroup bg = new ButtonGroup();
        bg.add(uriFragmentRadioButton);
        bg.add(qnameRendererRadioButton);
        bg.add(annotationValueRadioButton);


        Box box = new Box(BoxLayout.Y_AXIS);
        box.setBorder(ComponentFactory.createTitledBorder("Entity rendering"));
        box.add(uriFragmentRadioButton);
        box.add(Box.createVerticalStrut(4));

        JPanel qNamePanel = new JPanel(new BorderLayout());
        qNamePanel.setAlignmentX(0.0f);
        qNamePanel.add(qnameRendererRadioButton, BorderLayout.NORTH);

        JPanel showPrefixesPanel = new JPanel(new BorderLayout());
        showPrefixesPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));
        final JButton showPrefixedsButton = new JButton(new AbstractAction("Prefixes...") {
            public void actionPerformed(ActionEvent e) {
                PrefixMappingPanel.showDialog(getOWLEditorKit());
            }
        });
        showPrefixesPanel.add(showPrefixedsButton, BorderLayout.WEST);
        qNamePanel.add(showPrefixesPanel, BorderLayout.SOUTH);
        box.add(qNamePanel);
        qnameRendererRadioButton.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                showPrefixedsButton.setEnabled(qnameRendererRadioButton.isSelected());
            }
        });
        showPrefixedsButton.setEnabled(qnameRendererRadioButton.isSelected());

        box.add(Box.createVerticalStrut(4));
        box.add(annotationValueRadioButton);
        holderBox.add(box);


        highlightAOStatementsCheckBox = new JCheckBox("Highlight active ontology statements",
                                                      prefs.isHighlightActiveOntologyStatements());
        showHyperlinksCheckBox = new JCheckBox("Show hyperlinks in components that support them",
                                               prefs.isRenderHyperlinks());
        showChangedEntitiesCheckBox = new JCheckBox("Highlight changes entities", prefs.isHighlightChangedEntities());
        highlightKeyWordsCheckBox = new JCheckBox("Highlight keywords", prefs.isHighlightKeyWords());
        Box optBox = new Box(BoxLayout.Y_AXIS);
        optBox.setBorder(ComponentFactory.createTitledBorder("Appearance"));
        optBox.add(highlightAOStatementsCheckBox);
        optBox.add(showHyperlinksCheckBox);
        optBox.add(showChangedEntitiesCheckBox);
        optBox.add(highlightKeyWordsCheckBox);
        holderBox.add(optBox);
    }


    public void dispose() {
    }
}
