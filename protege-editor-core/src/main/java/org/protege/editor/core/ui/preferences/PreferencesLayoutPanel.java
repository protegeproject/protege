package org.protege.editor.core.ui.preferences;

import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 22/11/15
 */
public class PreferencesLayoutPanel extends JComponent {

    private static final Insets INSETS = new Insets(0, 0, 0, 0);

    private final JPanel backingPanel;

    private int currentRow = 0;

    public PreferencesLayoutPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        backingPanel = new JPanel();
        add(backingPanel);
        backingPanel.setLayout(new GridBagLayout());
    }

    public void addSeparator() {
        backingPanel.add(new JSeparator(),
                new GridBagConstraints(
                        0, currentRow,
                        3, 1,
                        100, 0,
                        GridBagConstraints.BASELINE_LEADING,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 0, 5, 0),
                        0, 0
                ));
        currentRow++;
    }

    public void addGroup(String groupLabel) {
        JLabel label = new JLabel(groupLabel);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        backingPanel.add(label,
                new GridBagConstraints(
                        0, currentRow,
                        1, 1,
                        0, 0,
                        GridBagConstraints.BASELINE_TRAILING,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 10),
                        0, 0
                ));
    }

    public void addGroupComponent(JComponent component) {
        backingPanel.add(component,
                new GridBagConstraints(
                        1, currentRow,
                        2, 1,
                        100, 0,
                        GridBagConstraints.BASELINE_LEADING,
                        GridBagConstraints.NONE,
                        INSETS,
                        0, 0
                ));
        if(component instanceof JTextField) {
            JTextField field = (JTextField) component;
            field.setMinimumSize(field.getPreferredSize());
        }
        currentRow++;
    }

    public void addIndentedGroupComponent(JComponent component) {
        backingPanel.add(component,
                new GridBagConstraints(
                        1, currentRow,
                        2, 1,
                        100, 0,
                        GridBagConstraints.BASELINE_LEADING,
                        GridBagConstraints.NONE,
                        new Insets(0, 20, 0, 0),
                        0, 0
                ));
        if(component instanceof JTextField) {
            JTextField field = (JTextField) component;
            field.setMinimumSize(field.getPreferredSize());
        }
        currentRow++;
    }

    public void addHelpText(String helpText) {
        JLabel label = new JLabel(helpText);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 10f));
        label.setForeground(Color.GRAY);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
        addGroupComponent(label);

    }

    public void addLabelledGroupComponent(String label, JComponent component) {
        backingPanel.add(new JLabel(label),
                new GridBagConstraints(
                        1, currentRow,
                        1, 1,
                        0, 0,
                        GridBagConstraints.BASELINE_TRAILING,
                        GridBagConstraints.NONE,
                        INSETS,
                        0, 0
                ));

        backingPanel.add(component,
                new GridBagConstraints(
                        2, currentRow,
                        1, 1,
                        100, 0,
                        GridBagConstraints.BASELINE_LEADING,
                        GridBagConstraints.NONE,
                        INSETS,
                        0, 0
                ));

        currentRow++;
    }

}
