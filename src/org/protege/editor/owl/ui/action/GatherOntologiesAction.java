package org.protege.editor.owl.ui.action;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.ui.GatherOntologiesPanel;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyFormat;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntologyStorageException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
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
 * Date: 22-May-2007<br><br>
 */
public class GatherOntologiesAction extends ProtegeOWLAction {

    public void actionPerformed(ActionEvent e) {
        // Need to pop a dialog asking where to save
        GatherOntologiesPanel panel = GatherOntologiesPanel.showDialog(getOWLEditorKit());
        if (panel == null) {
            return;
        }
        boolean errors = false;
        OWLOntologyFormat saveAsFormat = panel.getOntologyFormat();
        File saveAsLocation = panel.getSaveLocation();
        for (OWLOntology ont : panel.getOntologiesToSave()) {
            OWLOntologyFormat format = saveAsFormat;
            OWLOntologyManager man = getOWLModelManager().getOWLOntologyManager();
            if (format == null) {
                format = man.getOntologyFormat(ont);
            }
            URI originalPhysicalURI = man.getPhysicalURIForOntology(ont);
            String originalPath = originalPhysicalURI.getPath();
            if (originalPath == null) {
                originalPath = System.currentTimeMillis() + ".owl";
            }
            File originalFile = new File(originalPath);
            String originalFileName = originalFile.getName();
            File saveAsFile = new File(saveAsLocation, originalFileName);
            try {
                man.saveOntology(ont, format, saveAsFile.toURI());
            }
            catch (OWLOntologyStorageException e1) {
                ProtegeApplication.getErrorLog().handleError(Thread.currentThread(), e1);
                errors = true;
            }
        }
        if (errors) {
            JOptionPane.showMessageDialog(getWorkspace(),
                                          "There were errors when saving the ontologies.  Please check the log for details.",
                                          "Error during save",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() throws Exception {
    }
}
