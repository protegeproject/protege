package org.protege.editor.core.ui.util;

/*
* Copyright (C) 2007, University of Manchester
*
*
*/
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Mar 14, 2008<br><br>
 *
 * A JOptionPane that allows us to disable options to allow for user input
 * verification.
 */
public class VerifyingOptionPane extends JOptionPane {

    /**
     * 
     */
    private static final long serialVersionUID = -6308201481924625979L;

    private final Logger logger = LoggerFactory.getLogger(VerifyingOptionPane.class);

    private JButton okButton;


    public VerifyingOptionPane(JComponent c) {
        super(c, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
    }

    public VerifyingOptionPane(JComponent c, int messageType, int optionType) {
        super(c, messageType, optionType);
    }

    public void setOKEnabled(boolean enabled){
        if (okButton == null){
            okButton = getButtonComponent(this, JButton.class, (String)UIManager.get("OptionPane.okButtonText"));
        }
        if (okButton != null){
            okButton.setEnabled(enabled);
        }
        else{
            logger.error("Cannot find OK button for this system. " +
                    "Please report this with details of your Operating System and language.");
        }
    }

    private <T extends JComponent> T getButtonComponent(JComponent parent, Class<T> type, String name) {
        if (type.isAssignableFrom(parent.getClass())){
            if (parent instanceof JButton){
                if (name.equals(((JButton)parent).getText())){
                    return (T)parent;
                }
            }
        }
        for (Component c : parent.getComponents()){
            if (c instanceof JComponent){
                T target = getButtonComponent((JComponent)c, type, name);
                if (target != null){
                    return target;
                }
            }
        }
        return null;
    }
}
