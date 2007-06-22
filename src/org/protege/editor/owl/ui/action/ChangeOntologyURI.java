package org.protege.editor.owl.ui.action;

import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.OWLOntologyURIChanger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;
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
 * Bio-Health Informatics Group<br>
 * Date: 07-Mar-2007<br><br>
 */
public class ChangeOntologyURI extends ProtegeOWLAction {


    public void actionPerformed(ActionEvent e) {
        try {
            OWLOntology ont = getOWLModelManager().getActiveOntology();
            String s = JOptionPane.showInputDialog("New ontology URI", ont.getURI());

            if (s == null) {
                return;
            }
            URI uri = new URI(s);
            OWLOntologyURIChanger changer = new OWLOntologyURIChanger(getOWLModelManager().getOWLOntologyManager());
            getOWLModelManager().applyChanges(changer.getChanges(ont, uri));
        }
        catch (URISyntaxException ex) {
            JOptionPane.showMessageDialog(getWorkspace(), ex.getMessage(), "Invalid URI", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() throws Exception {
    }
}
