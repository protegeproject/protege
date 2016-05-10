package org.protege.editor.core.ui.preferences.node;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class BooleanPreferenceNode extends AbstractPreferenceNode<Boolean> {

    private Boolean value = Boolean.FALSE;

    private JCheckBox checkBox;


    public BooleanPreferenceNode(String label) {
        super(label);
        checkBox = new JCheckBox(label);
        checkBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setValue(checkBox.isSelected());
            }
        });
        checkBox.setSelected(getValue());
    }


    public Boolean getValue() {
        return value;
    }


    public void setValue(Boolean object) {
        value = object;
    }


    public JComponent getComponent() {
        return checkBox;
    }
}
