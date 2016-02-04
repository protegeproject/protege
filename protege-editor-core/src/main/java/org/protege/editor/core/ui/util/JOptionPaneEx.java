package org.protege.editor.core.ui.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 21-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class JOptionPaneEx {

    private static final Logger logger = LoggerFactory.getLogger(JOptionPaneEx.class);


    public static int showConfirmDialog(Component parent, String title, JComponent content, int messageType, int optionType, final JComponent defaultFocusedComponent) {

        JOptionPane optionPane = new JOptionPane(content, messageType, optionType) {
            @Override
            public void selectInitialValue() {
                // This makes focusing more consistent between
                // various operating systems / window managers
                if(defaultFocusedComponent != null) {
                    defaultFocusedComponent.requestFocusInWindow();
                }
            }
        };

        JDialog dlg = createDialog(parent, title, optionPane, defaultFocusedComponent);
        dlg.setVisible(true);
        return getReturnValueAsInteger(optionPane);
    }

    public static int showConfirmDialog(Component parent, String title, JComponent content, int messageType, int optionType, final JComponent defaultFocusedComponent, Object[] options, Object defaultOption) {

        JOptionPane optionPane = new JOptionPane(content, messageType, optionType, null, options, defaultOption) {
            @Override
            public void selectInitialValue() {
                // This makes focusing more consistent between
                // various operating systems / window managers
                if(defaultFocusedComponent != null) {
                    defaultFocusedComponent.requestFocusInWindow();
                }
            }
        };

        JDialog dlg = createDialog(parent, title, optionPane, defaultFocusedComponent);
        dlg.setVisible(true);
        return getReturnValueAsInteger(optionPane);
    }


    public static int showValidatingConfirmDialog(Component parent, String title, JComponent component, int messageType, int optionType, final JComponent defaultFocusedComponent) {
        if (component instanceof VerifiedInputEditor) {
            final VerifyingOptionPane optionPane = new VerifyingOptionPane(component, messageType, optionType) {

                public void selectInitialValue() {
                    // This is overridden so that the option pane dialog default
                    // button doesn't get the focus.
                    if (defaultFocusedComponent != null) {
                        defaultFocusedComponent.requestFocusInWindow();
                    }
                }
            };
            final InputVerificationStatusChangedListener verificationListener = optionPane::setOKEnabled;
            ((VerifiedInputEditor) component).addStatusChangedListener(verificationListener);

            final JDialog dlg = createDialog(parent, title, optionPane, defaultFocusedComponent);
            dlg.setModal(true);
            dlg.setVisible(true);
            return getReturnValueAsInteger(optionPane);
        }
        else {
            logger.warn("Component should implement VerifiedInputEditor for validating dialog to work. " + "Using normal dialog with no validating");
            return showConfirmDialog(parent, title, component, messageType, optionType, null);
        }
    }


    private static JDialog createDialog(Component parent, String title, JOptionPane optionPane, final JComponent defaultFocusedComponent) {
        JDialog dlg = optionPane.createDialog(parent, title);
        dlg.setLocationRelativeTo(parent);
        dlg.setResizable(true);
        dlg.pack();
        return dlg;
    }


    private static int getReturnValueAsInteger(JOptionPane optionPane) {
        Object value = optionPane.getValue();
        if(value == null) {
            return JOptionPane.CLOSED_OPTION;
        }
        Object[] options = optionPane.getOptions();
        if(options == null) {
            if(value instanceof Integer) {
                return (Integer) value;
            }
            else {
                return JOptionPane.CLOSED_OPTION;
            }
        }
        for(int i = 0; i < options.length; i++) {
            Object valueAtIndex = options[i];
            if(value.equals(valueAtIndex)) {
                return i;
            }
        }
        return JOptionPane.CLOSED_OPTION;
    }

}
