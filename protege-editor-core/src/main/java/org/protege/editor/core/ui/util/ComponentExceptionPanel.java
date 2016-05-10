package org.protege.editor.core.ui.util;


import org.eclipse.core.runtime.IExtension;

import javax.swing.*;
import java.awt.*;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 22, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
class ComponentExceptionPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -975497929104417874L;
    public static final int BORDER_THICKNESS = 4;


    public ComponentExceptionPanel(String message, Throwable exception, IExtension extension) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(BORDER_THICKNESS,
                                                                                     BORDER_THICKNESS,
                                                                                     BORDER_THICKNESS,
                                                                                     BORDER_THICKNESS),
                                                     BorderFactory.createMatteBorder(1, 1, 1, 1, Color.RED)));
        JLabel label = new JLabel("<html><body>" + message + "<br>" + exception.getClass().getSimpleName() + ":<br>" + exception.getMessage() + "<br>" + "</body></html>",
                                  JLabel.CENTER);
        label.setForeground(Color.RED);
        add(label, BorderLayout.NORTH);
    }
}
