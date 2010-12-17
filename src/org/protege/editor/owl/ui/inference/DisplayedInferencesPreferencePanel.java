package org.protege.editor.owl.ui.inference;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.EnumMap;
import java.util.Map.Entry;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.protege.editor.owl.model.inference.ReasonerPreferences;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

public class DisplayedInferencesPreferencePanel extends OWLPreferencesPanel {
    private static final long serialVersionUID = 8356095374634408957L;
    public static final String LABEL = "Displayed Inferences";
    private ReasonerPreferences preferences;
    private EnumMap<OptionalInferenceTask, JCheckBox> enabledMap = new EnumMap<OptionalInferenceTask, JCheckBox>(OptionalInferenceTask.class);

    public void initialise() throws Exception {
    	preferences = getOWLModelManager().getReasonerPreferences();
    	
    	setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
    	
    	// Add help text at top
    	c.gridx = 0;
    	c.gridy = 0;
    	c.gridwidth = 2;
    	c.gridheight = 1;
    	c.fill = GridBagConstraints.BOTH;
    	c.insets = new Insets(0,0,17,0);
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
    	c.weightx = 1.0; // request any extra horizontal space
    	JComponent help = PrecomputePreferencesPanel.buildHelp("/DisplayedInferencesHelp.txt");
        if (help != null) {
        	add(help, c);
        }
        
        buildClassFrameSectionPreferences(c);
        buildObjectPropertyFrameSectionPreferences(c);
        buildDataPropertyFrameSectionPreferences(c);
        buildIndividualFrameSectionPreferences(c);
    }

    @Override
    public void applyChanges() {
        for (Entry<OptionalInferenceTask, JCheckBox> entry : enabledMap.entrySet()) {
            OptionalInferenceTask task = entry.getKey();
            JCheckBox enabledBox = entry.getValue();
            preferences.setEnabled(task, enabledBox.isSelected());
        }
        preferences.save();
    }
    
    public void dispose() throws Exception {
        enabledMap = null;
    }
    
    private void buildClassFrameSectionPreferences(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0,0,0,12);
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 0.0; // request any extra horizontal space
        add((new JLabel("Displayed Class Inferences:")), c);
        
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(0,0,5,0);
        c.weightx = 1.0;
        add(getCheckBox(OptionalInferenceTask.SHOW_CLASS_UNSATISFIABILITY, "Unsatisfiability"), c);
        
        c.gridy = 2;
        add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_EQUIVALENT_CLASSES, "Equivalent Classes"), c);
        
        c.gridy = 3;
        add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_SUPER_CLASSES, "Superclasses"), c);
        
        c.gridy = 4;
        add(getCheckBox(OptionalInferenceTask.SHOW_INFERED_CLASS_MEMBERS, "Class Members"), c);
        
        c.gridy = 5;
        add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_INHERITED_ANONYMOUS_CLASSES, "Inherited Classes"), c);
    }
    
    private void buildObjectPropertyFrameSectionPreferences(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(11,0,0,12);
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 0.0; // request any extra horizontal space
        add((new JLabel("Displayed Object Property Inferences:")), c);
        
        c.gridx = 1;
        c.gridy = 6;
        c.insets = new Insets(11,0,5,0);
        c.weightx = 1.0;
        add(getCheckBox(OptionalInferenceTask.SHOW_OBJECT_PROPERTY_UNSATISFIABILITY, "Unsatisfiability"), c);
        
        c.gridy = 7;
        c.insets = new Insets(0,0,5,0);
        add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_OBJECT_PROPERTY_DOMAINS, "Domains"), c);
        
        c.gridy = 8;
        add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_OBJECT_PROPERTY_RANGES, "Ranges"), c);
        
        c.gridy = 9;
        add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_EQUIVALENT_OBJECT_PROPERTIES, "Equivalent Properties"), c);
        
        c.gridy = 10;
        add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_SUPER_OBJECT_PROPERTIES, "Super Properties"), c);
        
        c.gridy = 11;
        add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_INVERSE_PROPERTIES, "Inverses"), c);
    }
    
    private void buildDataPropertyFrameSectionPreferences(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = 12;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(11,0,0,12);
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 0.0; // request any extra horizontal space
        add((new JLabel("Displayed Data Property Inferences:")), c);
        
        c.gridx = 1;
        c.gridy = 12;
        c.insets = new Insets(11,0,5,0);
        c.weightx = 1.0;
        add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_DATATYPE_PROPERTY_DOMAINS, "Domains"), c);
        
        c.gridy = 13;
        c.insets = new Insets(0,0,5,0);
        add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_EQUIVALENT_DATATYPE_PROPERTIES, "Equivalent Properties"), c);
        
        c.gridy = 14;
        add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_SUPER_DATATYPE_PROPERTIES, "Super Properties"), c);
    }
    
    private void buildIndividualFrameSectionPreferences(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = 15;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(11,0,0,12);
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 0.0; // request any extra horizontal space
        add((new JLabel("Displayed Individual Inferences:")), c);
        
        c.gridx = 1;
        c.gridy = 15;
        c.insets = new Insets(11,0,5,0);
        c.weightx = 1.0;
        add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_TYPES, "Types"), c);
        
        c.gridy = 16;
        c.insets = new Insets(0,0,5,0);
        add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_OBJECT_PROPERTY_ASSERTIONS, "Object Property Assertions"), c);
        
        c.gridy = 17;
        add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_DATA_PROPERTY_ASSERTIONS, "Data Property Assertions"), c);
        
        c.gridy = 18;
        c.weighty = 1.0; // request any extra vertical space
        add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_SAMEAS_INDIVIDUAL_ASSERTIONS, "Same Individuals"), c);
    }
    
    private JCheckBox getCheckBox(OptionalInferenceTask task, String description) {    
        JCheckBox enabledBox = enabledMap.get(task);
        if (enabledBox == null) {
			description = description + " ("
					+ timeToString(preferences.getTimeInTask(task)) + " total/"
					+ timeToString(preferences.getAverageTimeInTask(task))
					+ " average)";
            enabledBox = new JCheckBox(description);
            enabledBox.setSelected(preferences.isEnabled(task));
            enabledMap.put(task, enabledBox);
        }
        return enabledBox;
    }
    
    private String timeToString(int milliseconds) {
        StringBuffer buffer = new StringBuffer();
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (60 * 1000));
        if (minutes != 0) {
            buffer.append(minutes);
            buffer.append(" min ");
        }
        if (seconds != 0) {
            buffer.append(seconds);
            buffer.append(" sec");
        }
        if (minutes == 0 && seconds == 0) {
            buffer.append(milliseconds);
            buffer.append(" ms");
        }
        return buffer.toString();
    }
}
