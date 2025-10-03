package org.protege.editor.core.ui.preferences;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 22/11/15
 */
public class PreferencesLayoutPanel extends JComponent {

    private static final Insets INSETS = new Insets(0, 0, 0, 0);

    private final JPanel backingPanel;

    private int currentRow = 0;

    private int componentCol = 1;

    private List<JRadioButton> currentButtonRun = new ArrayList<>();

    public PreferencesLayoutPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        Color panelBg = UIManager.getColor("Panel.background");
        if (panelBg == null) {
            panelBg = Color.WHITE;
        }
        setBackground(panelBg);
        backingPanel = new JPanel(new GridBagLayout());
        backingPanel.setBackground(panelBg);
        backingPanel.setOpaque(true);
        add(backingPanel, BorderLayout.NORTH);
    }

    public void setUseVerticalLabelling(boolean useVerticalLabelling) {
        if(useVerticalLabelling) {
            componentCol = 0;
        }
        else {
            componentCol = 1;
        }
    }

    @Override
    public Dimension getMinimumSize() {
        return backingPanel.getPreferredSize();
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

    public void addVerticalPadding() {
        backingPanel.add(Box.createVerticalStrut(12),
                new GridBagConstraints(
                        0, currentRow,
                        3, 1,
                        100, 0,
                        GridBagConstraints.BASELINE_LEADING,
                        GridBagConstraints.HORIZONTAL,
                        INSETS,
                        0, 0
                ));
        currentRow++;
    }

    public void addGroup(String groupLabel) {
        JLabel label = new JLabel(groupLabel);
        int alignment = isUseVerticalLabelling() ? GridBagConstraints.ABOVE_BASELINE_LEADING : GridBagConstraints.ABOVE_BASELINE_TRAILING;
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        backingPanel.add(label,
                new GridBagConstraints(
                        0, currentRow,
                        1, 1,
                        0, 0,
                        alignment,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 10),
                        0, 0
                ));
        if(isUseVerticalLabelling()) {
            currentRow++;
        }
    }

    private boolean isUseVerticalLabelling() {
        return componentCol == 0;
    }

    public void addGroupComponent(JComponent component) {
        handleComponentAdded(component);
        Insets insets;
        if(currentRow == 0) {
            insets = INSETS;
        }
        else if(component instanceof JRadioButton) {
            insets = INSETS;
        }
        else if(component instanceof JCheckBox) {
            insets = INSETS;
        }
        else {
            insets = new Insets(4, 0, 4, 0);
        }
        backingPanel.add(component,
                new GridBagConstraints(
                        componentCol, currentRow,
                        2, 1,
                        100, 0,
                        GridBagConstraints.BASELINE_LEADING,
                        GridBagConstraints.NONE,
                        insets,
                        0, 0
                ));
        if(component instanceof JTextField) {
            JTextField field = (JTextField) component;
            field.setMinimumSize(field.getPreferredSize());
        }
        currentRow++;
    }

    public void closeCurrentButtonRun() {
        if (currentButtonRun.isEmpty()) {
            return;
        }
        ButtonGroup bg = new ButtonGroup();
        for(JRadioButton button : currentButtonRun) {
            bg.add(button);
        }
        currentButtonRun.clear();
    }

    public void addIndentedGroupComponent(JComponent component) {
        handleComponentAdded(component);
        backingPanel.add(component,
                new GridBagConstraints(
                        componentCol, currentRow,
                        2, 1,
                        100, 0,
                        GridBagConstraints.BASELINE_LEADING,
                        GridBagConstraints.NONE,
                        new Insets(0, 50, 0, 0),
                        0, 0
                ));
        if(component instanceof JTextField) {
            JTextField field = (JTextField) component;
            field.setMinimumSize(field.getPreferredSize());
        }
        currentRow++;
    }

    private void handleComponentAdded(JComponent component) {
        if(component instanceof JRadioButton) {
            currentButtonRun.add((JRadioButton) component);
        }
    }

    public void addHelpText(String helpText) {
        JLabel label = new JLabel(helpText);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 10f));
        Color helpFg = UIManager.getColor("Label.disabledForeground");
        if (helpFg == null) {
            helpFg = Color.GRAY;
        }
        label.setForeground(helpFg);
        label.setBorder(BorderFactory.createEmptyBorder(3, 20, 7, 0));
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
        if(isUseVerticalLabelling()) {
            currentRow++;
        }
        backingPanel.add(component,
                new GridBagConstraints(
                        componentCol, currentRow,
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
