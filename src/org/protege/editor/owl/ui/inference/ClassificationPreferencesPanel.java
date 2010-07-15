package org.protege.editor.owl.ui.inference;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.protege.editor.owl.model.inference.ReasonerPreferences;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.owlapi.reasoner.InferenceType;

public class ClassificationPreferencesPanel extends OWLPreferencesPanel {
    private static final long serialVersionUID = -8812068573828834020L;
    private ReasonerPreferences preferences;
    private Map<InferenceType, JCheckBox> selectedInferences = new EnumMap<InferenceType, JCheckBox>(InferenceType.class);
    
    public void initialise() throws Exception {
        setLayout(new BorderLayout());
        preferences = getOWLModelManager().getOWLReasonerManager().getReasonerPreferences();
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    public void dispose() throws Exception {

    }

    @Override
    public void applyChanges() {
        preferences.setDefaultClassificationInferenceTypes(getPreCompute());
        preferences.save();
    }
    
    private JPanel createCenterPanel() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.PAGE_AXIS));
        center.add(new JLabel("These preferences control what inferences are precomputed"));
        center.add(new JLabel("when the user clicks on the classify button."));
        center.add(Box.createRigidArea(new Dimension(0,10)));
        
        for (InferenceType type : InferenceType.values()) {
            JCheckBox check = new JCheckBox(getInferenceTypeName(type));
            selectedInferences.put(type, check);
            check.setAlignmentX(Component.LEFT_ALIGNMENT);
            check.setSelected(preferences.getDefaultClassificationInferenceTypes().contains(type));
            center.add(check);
        }
        return center;
    }
    
    
    public Set<InferenceType> getPreCompute() {
        Set<InferenceType> preCompute = EnumSet.noneOf(InferenceType.class);
        for (Entry<InferenceType, JCheckBox> entry : selectedInferences.entrySet()) {
            JCheckBox check = entry.getValue();
            if (check.isSelected()) {
                preCompute.add(entry.getKey());
            }
        }
        return preCompute;
    }
    
    private String getInferenceTypeName(InferenceType type) {
        switch (type)  {
        case CLASS_ASSERTIONS:           return "Inferred Individuals and types";
        case CLASS_HIERARCHY:            return "Inferred Class Hierarchy";
        case DATA_PROPERTY_ASSERTIONS:   return "Inferred Data Property Assertions (incomplete)";
        case DATA_PROPERTY_HIERARCHY:    return "Inferred Data Property Hierarchy";
        case OBJECT_PROPERTY_ASSERTIONS: return "Inferred Object Property Values";
        case OBJECT_PROPERTY_HIERARCHY:  return "Inferred Object Property Hierarchy";
        default:                         throw new IllegalStateException("Programmer error");
        }
    }

}
