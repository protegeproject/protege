package org.protege.editor.owl.ui.inference;

import java.awt.Dimension;
import java.util.EnumMap;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

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
        JComponent help = buildHelp();
        add(help);
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
    
    private JComponent buildHelp() {
        Box help = new Box(BoxLayout.Y_AXIS);
        help.setBorder(ComponentFactory.createTitledBorder("Description"));
        help.setAlignmentX(0.0f);
        help.add(new JLabel("These preferences control what inferences are displayed while"));
        help.add(new JLabel("navigating around in Protege.  Each type of inference includes an"));
        help.add(new JLabel("estimate of the total time that Protege has spent doing that"));
        help.add(new JLabel("inference.  These times are dynamically updated as different owl"));
        help.add(new JLabel("entities are selected and inference mechanisms are invoked.  Thus if"));
        help.add(new JLabel("navigating a tab is becoming expensive and you believe that the"));
        help.add(new JLabel("automatic inferences may be the culprit, then you can come to this"));
        help.add(new JLabel("preferences panel to determine which inferences are causing the delay"));
        return help;
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
