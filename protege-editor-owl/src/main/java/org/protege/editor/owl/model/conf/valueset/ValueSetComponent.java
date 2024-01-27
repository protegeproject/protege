package org.protege.editor.owl.model.conf.valueset;

import java.awt.BorderLayout;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 30 Aug 2017
 */
public class ValueSetComponent extends JPanel {

    private static final int THRESHOLD_SIZE = 6;

    private JComboBox<LabelledValue> comboBox = new JComboBox<>();

    private Box radioButtonBox = new Box(BoxLayout.Y_AXIS);

    private Map<JRadioButton, LabelledValue> radioButtons = new LinkedHashMap<>();

    public ValueSetComponent() {
        setLayout(new BorderLayout());
        comboBox.setRenderer(new LabelledValueRenderer());
    }

    public void clearValues() {
        removeAll();
        radioButtonBox.removeAll();
        radioButtons.clear();
    }

    public void setValues(@Nonnull List<LabelledValue> labelledValues) {
        removeAll();
        radioButtons.clear();
        radioButtonBox.removeAll();
        if (labelledValues.size() < THRESHOLD_SIZE) {
            add(radioButtonBox, BorderLayout.NORTH);
            ButtonGroup bg = new ButtonGroup();
            labelledValues.forEach(v -> {
                JRadioButton b = new JRadioButton(v.getLabel());
                bg.add(b);
                radioButtons.put(b, v);
                radioButtonBox.add(b);
            });
        }
        else {
            comboBox.setModel(new DefaultComboBoxModel<>(labelledValues.toArray(new LabelledValue[labelledValues.size()])));
            add(comboBox, BorderLayout.NORTH);
        }
    }

    @Nonnull
    public Optional<LabelledValue> getSelectedValue() {
        if (radioButtons.isEmpty()) {
            return Optional.ofNullable((LabelledValue) comboBox.getSelectedItem());
        }
        else {
            return radioButtons.entrySet().stream()
                               .filter(e -> e.getKey().isSelected())
                               .map(Map.Entry::getValue)
                               .findFirst();
        }
    }

    public void setSelectedIndex(int selectedIndex) {
        if(radioButtons.isEmpty()) {
            if(selectedIndex < comboBox.getModel().getSize()) {
                comboBox.setSelectedIndex(selectedIndex);
            }
        }
        else {
            int counter = 0;
            for(JRadioButton button : radioButtons.keySet()) {
                if(selectedIndex == counter) {
                    button.setSelected(true);
                    break;
                }
                counter++;
            }
        }
    }
}
