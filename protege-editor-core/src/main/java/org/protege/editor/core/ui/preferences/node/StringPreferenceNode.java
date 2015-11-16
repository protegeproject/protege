package org.protege.editor.core.ui.preferences.node;

import javax.swing.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class StringPreferenceNode extends AbstractPreferenceNode<String> {

    private JTextField textField;


    public StringPreferenceNode(String label) {
        super(label);
        textField = new JTextField(20);
    }


    public String getValue() {
        return textField.getText();
    }


    public void setValue(String object) {
        textField.setText(object);
    }


    public JComponent getComponent() {
        return textField;
    }
}
