package org.protege.editor.owl.ui.inference;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.protege.editor.owl.model.inference.ReasonerPreferences;
import org.semanticweb.owlapi.reasoner.InferenceType;

public class ChoosePreComputedInferencesDialog extends JDialog {
    private ReasonerPreferences preferences;
    private Map<InferenceType, JCheckBox> selectedInferences = new EnumMap<InferenceType, JCheckBox>(InferenceType.class);
    private boolean cancelled = false;
    
    public ChoosePreComputedInferencesDialog(Frame owner, ReasonerPreferences preferences) {
        super(owner, "Pre-Compute Inferences Dialog");
        this.preferences = preferences;
        
        JPanel contents = new JPanel();
        contents.setLayout(new BorderLayout());
        contents.add(createCenterPanel(), BorderLayout.CENTER);
        contents.add(createBottomPanel(), BorderLayout.SOUTH);
        
        add(contents);
    }
    
    private JPanel createCenterPanel() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.PAGE_AXIS));
        for (InferenceType type : InferenceType.values()) {
            JCheckBox check = new JCheckBox(getInferenceTypeName(type));
            selectedInferences.put(type, check);
            check.setAlignmentX(Component.LEFT_ALIGNMENT);
            check.setSelected(preferences.getAutoPreComputed().contains(type));
            center.add(check);
        }
        return center;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottom = new JPanel();
        bottom.setLayout(new FlowLayout());
        
        bottom.add(new JButton(new AbstractAction("Set default") {
            private static final long serialVersionUID = -3505728500677504314L;

            public void actionPerformed(ActionEvent e) {
                preferences.setAutoPreComputed(getPreCompute());
            }
            
        }));
        bottom.add(new JButton(new AbstractAction("Apply") {
            private static final long serialVersionUID = -6180758367443597436L;

            public void actionPerformed(ActionEvent e) {
                cancelled = false;
                setVisible(false);
            }
        }));
        
        bottom.add(new JButton(new AbstractAction("Cancel") {
            private static final long serialVersionUID = 7792551962262186747L;

            public void actionPerformed(ActionEvent e) {
                cancelled = true;
                setVisible(false);
            }
        }));
        
        return bottom;
    }
    
    public boolean isCancelled() {
        return cancelled;
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
        case CLASS_ASSERTIONS:           return "Precompute Class Assertions";
        case CLASS_HIERARCHY:            return "Precompute Class Hierarchy";
        case DATA_PROPERTY_ASSERTIONS:   return "Precompute Data Property Assertions (incomplete)";
        case DATA_PROPERTY_HIERARCHY:    return "Precompute Data Property Hierarchy";
        case OBJECT_PROPERTY_ASSERTIONS: return "Precompute Object Property Assertions";
        case OBJECT_PROPERTY_HIERARCHY:  return "Precompute Object Property Hierarchy";
        default:                         throw new IllegalStateException("Programmer error");
        }
    }
}
