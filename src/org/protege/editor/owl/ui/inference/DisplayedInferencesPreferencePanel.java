package org.protege.editor.owl.ui.inference;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.model.inference.ReasonerPreferences;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

public class DisplayedInferencesPreferencePanel extends OWLPreferencesPanel {
    private static final long serialVersionUID = 8356095374634408957L;
    public static final String LABEL = "Displayed Inferences";
    private ReasonerPreferences preferences;
    private EnumMap<OptionalInferenceTask, JCheckBox> enabledMap = new EnumMap<OptionalInferenceTask, JCheckBox>(OptionalInferenceTask.class);


    public void initialise() throws Exception {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JComponent help = PrecomputePreferencesPanel.buildHelp("DisplayedInferencesHelp.txt");
        if (help != null) {
        	add(help);
        }
        add(Box.createRigidArea(new Dimension(0,10)));

        preferences = getOWLModelManager().getReasonerPreferences();
        JComponent[] boxes = {buildClassFrameSectionPreferences(), 
                buildObjectPropertyFrameSectionPreferences(),
                buildDataPropertyFrameSectionPreferences(),
                buildIndividualFrameSectionPreferences()
        };
        for (JComponent box : boxes) {
            add(box);
        }
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
        classFrameSectionPreferences.setBorder(ComponentFactory.createTitledBorder("Displayed Class Inferences"));
        classFrameSectionPreferences.setAlignmentX(0.0f);
        classFrameSectionPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_CLASS_UNSATISFIABILITY, "Unsatisfiability"));
        classFrameSectionPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_EQUIVALENT_CLASSES, "Equivalent Classes"));
        classFrameSectionPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_SUPER_CLASSES, "Superclasses"));
        classFrameSectionPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERED_CLASS_MEMBERS, "Class Members"));
        classFrameSectionPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_INHERITED_ANONYMOUS_CLASSES, "Inherited Classes"));
        return classFrameSectionPreferences;
    }
    
    private JComponent buildObjectPropertyFrameSectionPreferences() {
        Box objectPropertyTabPreferences = new Box(BoxLayout.Y_AXIS);
        objectPropertyTabPreferences.setBorder(ComponentFactory.createTitledBorder("Displayed Object Property Inferences"));
        objectPropertyTabPreferences.setAlignmentX(0.0f);
        objectPropertyTabPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_OBJECT_PROPERTY_UNSATISFIABILITY, "Unsatisfiability"));
        objectPropertyTabPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_OBJECT_PROPERTY_DOMAINS, "Domains"));
        objectPropertyTabPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_OBJECT_PROPERTY_RANGES, "Ranges"));
        objectPropertyTabPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_EQUIVALENT_OBJECT_PROPERTIES, "Equivalent Properties"));
        objectPropertyTabPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_SUPER_OBJECT_PROPERTIES, "Super Properties"));
        objectPropertyTabPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_INVERSE_PROPERTIES, "Inverses"));
        return objectPropertyTabPreferences;
    }
    
    private JComponent buildDataPropertyFrameSectionPreferences() {
        Box dataPropertyTabPreferences = new Box(BoxLayout.Y_AXIS);
        dataPropertyTabPreferences.setBorder(ComponentFactory.createTitledBorder("Displayed Data Property Inferences"));
        dataPropertyTabPreferences.setAlignmentX(0.0f);
        dataPropertyTabPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_DATATYPE_PROPERTY_DOMAINS, "Domains"));
        dataPropertyTabPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_EQUIVALENT_DATATYPE_PROPERTIES, "Equivalent Properties"));
        dataPropertyTabPreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_SUPER_DATATYPE_PROPERTIES, "Super Properties"));
        return dataPropertyTabPreferences;
    }
    
    private JComponent buildIndividualFrameSectionPreferences() {
        Box individualInferencePreferences = new Box(BoxLayout.Y_AXIS);
        individualInferencePreferences.setBorder(ComponentFactory.createTitledBorder("Displayed Individual Inferences"));
        individualInferencePreferences.setAlignmentX(0.0f);
        individualInferencePreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_TYPES, "Types"));
        individualInferencePreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_OBJECT_PROPERTY_ASSERTIONS, "Object Property Assertions"));
        individualInferencePreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_DATA_PROPERTY_ASSERTIONS, "Data Property Assertions"));
        individualInferencePreferences.add(getCheckBox(OptionalInferenceTask.SHOW_INFERRED_SAMEAS_INDIVIDUAL_ASSERTIONS, "Same Individuals"));        
        return individualInferencePreferences;
    }
    
    private JCheckBox getCheckBox(OptionalInferenceTask task, String description) {    
        JCheckBox enabledBox = enabledMap.get(task);
        if (enabledBox == null) {
            description = description + " (" + timeToString(preferences.getTimeInTask(task)) + " total/" + timeToString(preferences.getAverageTimeInTask(task)) + " average)";
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
