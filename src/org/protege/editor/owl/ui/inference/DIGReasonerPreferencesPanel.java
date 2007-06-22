package org.protege.editor.owl.ui.inference;

import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

import javax.swing.*;
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
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 30-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DIGReasonerPreferencesPanel extends OWLPreferencesPanel {

    private static final Logger logger = Logger.getLogger(DIGReasonerPreferencesPanel.class);


    private JTextField reasonerURLField;


    public void applyChanges() {
//        try {
//            URL url = new URL(reasonerURLField.getText());
//            URL curURL = DIGReasonerPreferences.getInstance().getReasonerURL();
//            if(!curURL.equals(url)) {
//                DIGReasonerPreferences.getInstance().setReasonerURL(url);
//            }
//        } catch (MalformedURLException e) {
//            logger.warn("Malformed reasoner URL: " + e.getMessage());
//        }
    }


    public void initialise() throws Exception {
        //DIGReasonerPreferences prefs = DIGReasonerPreferences.getInstance();
//        URL url = prefs.getReasonerURL();
//        reasonerURLField = new JTextField();
//        setLayout(new BorderLayout());
//        JPanel holder = new JPanel(new BorderLayout(3, 3));
//        holder.add(new JLabel("Reasoner URL"), BorderLayout.NORTH);
//        holder.add(reasonerURLField, BorderLayout.SOUTH);
//        add(holder, BorderLayout.NORTH);
//        reasonerURLField.setText(url.toString());
    }


    public void dispose() {
    }
}
