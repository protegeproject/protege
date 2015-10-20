package org.protege.editor.owl.ui.renderer.conf;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.preferences.PreferencesPanelLayoutManager;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.editor.owl.ui.renderer.plugin.RendererPlugin;
import org.semanticweb.owlapi.model.OWLRuntimeException;

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

    private Map<JRadioButton, RendererPlugin> buttonToRendererMap = new LinkedHashMap<JRadioButton, RendererPlugin>();

    private JList annotationPropertiesList;

    private JCheckBox highlightAOStatementsCheckBox;

    private JCheckBox showHyperlinksCheckBox;

    private JCheckBox highlightKeyWordsCheckBox;

    private JCheckBox useThatAsSynonymForAndCheckBox;

    private JSpinner fontSizeSpinner;

    private JButton configureButton;

    private RendererPlugin originalRendererPlugin;

    private boolean dirty = false;


    public void applyChanges() {
        OWLRendererPreferences prefs = OWLRendererPreferences.getInstance();
        prefs.setHighlightActiveOntologyStatements(highlightAOStatementsCheckBox.isSelected());
        prefs.setRenderHyperlinks(showHyperlinksCheckBox.isSelected());
        prefs.setHighlightKeyWords(highlightKeyWordsCheckBox.isSelected());
        prefs.setUseThatKeyword(useThatAsSynonymForAndCheckBox.isSelected());
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

        fontSizePanel.add(new JButton(new AbstractAction("Reset font") {
        	public void actionPerformed(ActionEvent arg0) {
        		resetFont();
        	}
        }));

        holderBox.add(fontSizePanel);
    }

    protected void resetFont() {
    	OWLRendererPreferences prefs = OWLRendererPreferences.getInstance();
    	prefs.setFontSize(OWLRendererPreferences.DEFAULT_FONT_SIZE);
    	fontSizeSpinner.setValue(OWLRendererPreferences.DEFAULT_FONT_SIZE);
	}


	private Component createRendererSelectionPanel() {
        OWLRendererPreferences prefs = OWLRendererPreferences.getInstance();

        for (RendererPlugin plugin : prefs.getRendererPlugins()) {
        	addRenderer(plugin.getName(), plugin);
        }
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

        configureButton = new JButton(new AbstractAction("Configure...") {
            public void actionPerformed(ActionEvent e) {
            	RendererPlugin plugin = getSelectedRendererPlugin();
            	try {
            		if (plugin != null && plugin.newInstance().configure(getOWLEditorKit())) {
            			dirty = true;            		
            		}
            	}
            	catch (Exception cnfe) {
            		ProtegeApplication.getErrorLog().logError(cnfe);
            	}
            }
        });
        optsPanel.add(configureButton);

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
    			ProtegeApplication.getErrorLog().logError(e);
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
