package org.protege.editor.core.ui.util;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
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

    private static final Logger logger = Logger.getLogger(VerifyingOptionPane.class);

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
            logger.warn("Cannot find OK button for this system. Please report this with details of your OS and language.");
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
