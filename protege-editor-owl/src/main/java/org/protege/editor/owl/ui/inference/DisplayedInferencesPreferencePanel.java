package org.protege.editor.owl.ui.inference;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.owl.model.inference.ReasonerPreferences;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map.Entry;

public class DisplayedInferencesPreferencePanel extends OWLPreferencesPanel {
    private static final long serialVersionUID = 8356095374634408957L;
    public static final String LABEL = "Displayed Inferences";
    private ReasonerPreferences preferences;
    private EnumMap<OptionalInferenceTask, JCheckBox> enabledMap = new EnumMap<OptionalInferenceTask, JCheckBox>(OptionalInferenceTask.class);

    public void initialise() throws Exception {
        setLayout(new BorderLayout());
        PreferencesLayoutPanel layoutPanel = new PreferencesLayoutPanel();
        add(layoutPanel, BorderLayout.NORTH);
    	preferences = getOWLModelManager().getReasonerPreferences();
    	
//    	JComponent help = PrecomputePreferencesPanel.buildHelp("/DisplayedInferencesHelp.txt");
//        if (help != null) {
//            layoutPanel.addGroupComponent(help);
//        }

        buildClassFrameSectionPreferences(layoutPanel);
        layoutPanel.addSeparator();
        buildObjectPropertyFrameSectionPreferences(layoutPanel);
        layoutPanel.addSeparator();
        buildDataPropertyFrameSectionPreferences(layoutPanel);
        layoutPanel.addSeparator();
        buildIndividualFrameSectionPreferences(layoutPanel);
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
    
    private void buildClassFrameSectionPreferences(PreferencesLayoutPanel layoutPanel) {
        layoutPanel.addGroup("Class inferences");

        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_CLASS_UNSATISFIABILITY, "Satisfiability"));
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_EQUIVALENT_CLASSES, "Equivalent Classes"));
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_SUPER_CLASSES, "Superclasses"));
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_INFERED_CLASS_MEMBERS, "Class Instances"));
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_DISJOINT_CLASSES, "Disjoint Classes"));

    }
    
    private void buildObjectPropertyFrameSectionPreferences(PreferencesLayoutPanel layoutPanel) {
        layoutPanel.addGroup("Object property inferences");
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_OBJECT_PROPERTY_UNSATISFIABILITY, "Satisfiability"));
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_OBJECT_PROPERTY_DOMAINS, "Domains"));
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_OBJECT_PROPERTY_RANGES, "Ranges"));
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_EQUIVALENT_OBJECT_PROPERTIES, "Equivalent Properties"));
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_SUPER_OBJECT_PROPERTIES, "Super Properties"));
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_INVERSE_PROPERTIES, "Inverse properties"));
    }
    
    private void buildDataPropertyFrameSectionPreferences(PreferencesLayoutPanel layoutPanel) {
        layoutPanel.addGroup("Data property inferences");
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_DATATYPE_PROPERTY_DOMAINS, "Domains"));
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_EQUIVALENT_DATATYPE_PROPERTIES, "Equivalent Properties"));
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_SUPER_DATATYPE_PROPERTIES, "Super Properties"));
    }
    
    private void buildIndividualFrameSectionPreferences(PreferencesLayoutPanel layoutPanel) {
        layoutPanel.addGroup("Individual inferences");
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_TYPES, "Types"));
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_OBJECT_PROPERTY_ASSERTIONS, "Object Property Assertions"));
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_DATA_PROPERTY_ASSERTIONS, "Data Property Assertions"));
        layoutPanel.addGroupComponent(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_SAMEAS_INDIVIDUAL_ASSERTIONS, "Same Individuals"));
    }
    
    private JCheckBox getCheckBox(OptionalInferenceTask task, String description) {    
        JCheckBox enabledBox = enabledMap.get(task);
        if (enabledBox == null) {
			description = "<html><body>" + description + " <span style='color: gray;'>("
					+ timeToString(preferences.getTimeInTask(task)) + " total/"
					+ timeToString(preferences.getAverageTimeInTask(task))
					+ " average)</span>";
            enabledBox = new JCheckBox(description);
            enabledBox.setSelected(preferences.isEnabled(task));
            enabledMap.put(task, enabledBox);
        }
        return enabledBox;
    }
    
    private String timeToString(int milliseconds) {
        StringBuilder sb = new StringBuilder();
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (60 * 1000));
        if (minutes != 0) {
            sb.append(minutes);
            sb.append(" min ");
        }
        if (seconds != 0) {
            sb.append(seconds);
            sb.append(" sec");
        }
        if (minutes == 0 && seconds == 0) {
            sb.append(milliseconds);
            sb.append(" ms");
        }
        return sb.toString();
    }
}
