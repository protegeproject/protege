package org.protege.editor.owl.ui.renderer.conf;

import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.editor.owl.ui.renderer.plugin.RendererPlugin;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RendererPreferencesPanel extends OWLPreferencesPanel {

    private final Logger logger = LoggerFactory.getLogger(RendererPreferencesPanel.class);
    private Map<JRadioButton, RendererPlugin> buttonToRendererMap = new LinkedHashMap<>();

    private JList annotationPropertiesList;

    private JCheckBox highlightAOStatementsCheckBox;

    private JCheckBox showHyperlinksCheckBox;

    private JCheckBox highlightKeyWordsCheckBox;

    private JSpinner fontSizeSpinner;

    private JButton configureButton;

    private RendererPlugin originalRendererPlugin;

    private boolean dirty = false;


    public void applyChanges() {
        OWLRendererPreferences prefs = OWLRendererPreferences.getInstance();
        prefs.setHighlightActiveOntologyStatements(highlightAOStatementsCheckBox.isSelected());
        prefs.setRenderHyperlinks(showHyperlinksCheckBox.isSelected());
        prefs.setHighlightKeyWords(highlightKeyWordsCheckBox.isSelected());
        prefs.setFontSize((Integer) fontSizeSpinner.getValue());
        if (isDirty()){
            RendererPlugin plugin = getSelectedRendererPlugin();
            try {
                prefs.setRendererPlugin(plugin);
                OWLModelManagerEntityRenderer ren = plugin.newInstance();
                getOWLModelManager().refreshRenderer();
            }
            catch (Exception e) {
                throw new OWLRuntimeException(e);
            }
        }
        getOWLEditorKit().getWorkspace().refreshComponents();
    }


    public void initialise() throws Exception {
        setLayout(new BorderLayout());

        PreferencesLayoutPanel layoutPanel = new PreferencesLayoutPanel();
        add(layoutPanel, BorderLayout.NORTH);

        createRendererSelectionPanel(layoutPanel);
        layoutPanel.addSeparator();

        layoutPanel.addGroup("Appearance");


        OWLRendererPreferences prefs = OWLRendererPreferences.getInstance();


        highlightAOStatementsCheckBox = new JCheckBox("Highlight active ontology statements",
                                                      prefs.isHighlightActiveOntologyStatements());
        showHyperlinksCheckBox = new JCheckBox("Show hyperlinks in components that support them",
                                               prefs.isRenderHyperlinks());
        highlightKeyWordsCheckBox = new JCheckBox("Highlight keywords", prefs.isHighlightKeyWords());

        layoutPanel.addGroupComponent(highlightAOStatementsCheckBox);
        layoutPanel.addGroupComponent(showHyperlinksCheckBox);
        layoutPanel.addGroupComponent(highlightKeyWordsCheckBox);

        layoutPanel.addSeparator();

        layoutPanel.addGroup("Font size");

        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(prefs.getFontSize(), 1, 120, 1));
        layoutPanel.addGroupComponent(fontSizeSpinner);
        JButton resetFontSizeButton = new JButton("Reset font");
        resetFontSizeButton.addActionListener(e -> resetFont());
        layoutPanel.addIndentedGroupComponent(resetFontSizeButton);
    }

    protected void resetFont() {
    	OWLRendererPreferences prefs = OWLRendererPreferences.getInstance();
    	prefs.setFontSize(OWLRendererPreferences.DEFAULT_FONT_SIZE);
    	fontSizeSpinner.setValue(OWLRendererPreferences.DEFAULT_FONT_SIZE);
	}


	private void createRendererSelectionPanel(PreferencesLayoutPanel layoutPanel) {
        OWLRendererPreferences prefs = OWLRendererPreferences.getInstance();

        for (RendererPlugin plugin : prefs.getRendererPlugins()) {
        	addRenderer(plugin.getName(), plugin);
        }
        layoutPanel.addGroup("Entity rendering");

        ButtonGroup bg = new ButtonGroup();
        for (JRadioButton button : buttonToRendererMap.keySet()){
            bg.add(button);
            layoutPanel.addGroupComponent(button);
            button.addChangeListener(e -> updateRendererButtons());
        }


        configureButton = new JButton("Configure...");
        configureButton.addActionListener(e -> {
            RendererPlugin plugin = getSelectedRendererPlugin();
            try {
                if (plugin != null && plugin.newInstance().configure(getOWLEditorKit())) {
                    dirty = true;
                }
            }
            catch (Exception cnfe) {
                logger.error("An error occurred whilst instantiating a renderer preferences panel plugin: {}", cnfe);
            }
        });
        layoutPanel.addIndentedGroupComponent(configureButton);

        updateRendererButtons();
    }


    private void addRenderer(String label, RendererPlugin plugin) {
        RendererPlugin currentPlugin = OWLRendererPreferences.getInstance().getRendererPlugin();
        JRadioButton button = new JRadioButton(label, plugin.equals(currentPlugin));
        buttonToRendererMap.put(button, plugin);
    }

    private void updateRendererButtons() {
    	RendererPlugin plugin = getSelectedRendererPlugin();
    	if (plugin != null) {
    		try {
        		configureButton.setEnabled(plugin.newInstance().isConfigurable());
    		}
    		catch (Exception e) {
    			logger.error("An error occurred whilst updating the state of a renderer plugin: {}", e);
                configureButton.setEnabled(false);
    		}
    	}
    }

    public void dispose() {
    }


    public boolean isDirty() {
        return dirty || (getSelectedRendererPlugin() != null && !getSelectedRendererPlugin().equals(originalRendererPlugin));
    }


    public RendererPlugin getSelectedRendererPlugin() {
        for (JRadioButton button : buttonToRendererMap.keySet()){
            if (button.isSelected()){
                return buttonToRendererMap.get(button);
            }
        }
        return null;
    }
}
