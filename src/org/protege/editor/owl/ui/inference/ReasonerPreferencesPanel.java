package org.protege.editor.owl.ui.inference;

import java.util.EnumMap;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.model.inference.ReasonerPreferences;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

public class ReasonerPreferencesPanel extends OWLPreferencesPanel {
    private static final long serialVersionUID = 8356095374634408957L;
    private ReasonerPreferences preferences;
    private EnumMap<OptionalInferenceTask, JCheckBox> enabledMap = new EnumMap<OptionalInferenceTask, JCheckBox>(OptionalInferenceTask.class);


    public void initialise() throws Exception {
        preferences = getOWLModelManager().getReasonerPreferences();
        add(buildClassFrameSectionPreferences());
        add(buildObjectPropertyFrameSectionPreferences());
        add(buildDataPropertyFrameSectionPreferences());
        add(buildIndividualFrameSectionPreferences());
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
    
    private JComponent buildClassFrameSectionPreferences() {
        Box classFrameSectionPreferences = new Box(BoxLayout.Y_AXIS);
        classFrameSectionPreferences.setBorder(ComponentFactory.createTitledBorder("Class Inferences"));
        classFrameSectionPreferences.setAlignmentX(0.0f);
        classFrameSectionPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_CLASS_UNSATISFIABILITY, "Unsatisfiability"));
        classFrameSectionPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_INHERITED_ANONYMOUS_CLASSES, "Inferred Inherited Classes"));
        return classFrameSectionPreferences;
    }
    
    private JComponent buildObjectPropertyFrameSectionPreferences() {
        Box objectPropertyTabPreferences = new Box(BoxLayout.Y_AXIS);
        objectPropertyTabPreferences.setBorder(ComponentFactory.createTitledBorder("Object Property Inferences"));
        objectPropertyTabPreferences.setAlignmentX(0.0f);
        objectPropertyTabPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_OBJECT_PROPERTY_UNSATISFIABILITY, "Unsatisfiability"));
        objectPropertyTabPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_OBJECT_PROPERTY_DOMAINS, "Inferred Domains"));
        objectPropertyTabPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_OBJECT_PROPERTY_RANGES, "Inferred Ranges"));
        objectPropertyTabPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_EQUIVALENT_OBJECT_PROPERTIES, "Inferred Equivalents"));
        objectPropertyTabPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_SUPER_OBJECT_PROPERTIES, "Inferred SuperProperties"));
        objectPropertyTabPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_INVERSE_PROPERTIES, "Inferred Inverses"));
        return objectPropertyTabPreferences;
    }
    
    private JComponent buildDataPropertyFrameSectionPreferences() {
        Box dataPropertyTabPreferences = new Box(BoxLayout.Y_AXIS);
        dataPropertyTabPreferences.setBorder(ComponentFactory.createTitledBorder("Data Property Inferences"));
        dataPropertyTabPreferences.setAlignmentX(0.0f);
        dataPropertyTabPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_DATATYPE_PROPERTY_DOMAINS, "Inferred Domains"));
        return dataPropertyTabPreferences;
    }
    
    private JComponent buildIndividualFrameSectionPreferences() {
        Box dataPropertyTabPreferences = new Box(BoxLayout.Y_AXIS);
        dataPropertyTabPreferences.setBorder(ComponentFactory.createTitledBorder("Individual Inferences"));
        dataPropertyTabPreferences.setAlignmentX(0.0f);
        return dataPropertyTabPreferences;
    }
    
    private JCheckBox getCheckBox(OptionalInferenceTask task, String description) {    
        JCheckBox enabledBox = enabledMap.get(task);
        if (enabledBox == null) {
            description = description + " (" + preferences.getTimeInTask(task) + " ms total/" + preferences.getAverageTimeInTask(task) + " ms average)";
            enabledBox = new JCheckBox(description);
            enabledBox.setSelected(preferences.isEnabled(task));
            enabledMap.put(task, enabledBox);
        }
        return enabledBox;
    }

}
