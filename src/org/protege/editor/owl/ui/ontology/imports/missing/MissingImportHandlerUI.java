package org.protege.editor.owl.ui.ontology.imports.missing;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.MissingImportHandler;
import org.protege.editor.owl.ui.UIHelper;

import javax.swing.*;
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
 * Medical Informatics Group<br>
 * Date: 31-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MissingImportHandlerUI implements MissingImportHandler {

    private static final Logger logger = Logger.getLogger(MissingImportHandlerUI.class);

    private OWLEditorKit owlEditorKit;


    public MissingImportHandlerUI(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
    }


    public URI getPhysicalURI(URI ontologyURI) {

        int ret = JOptionPane.showConfirmDialog(null,
                                                "<html><body>The system couldn't locate the ontology:<br><font color=\"blue\">" + ontologyURI.toString() + "</font><br><br>" +

                                                        "Would you like to attempt to resolve the missing import?</body></html>",
                                                "Resolve missing import?",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.WARNING_MESSAGE);
        if (ret != JOptionPane.YES_OPTION) {
            return ontologyURI;
        }
        UIHelper helper = new UIHelper(owlEditorKit);
        File file = helper.chooseOWLFile("Please select an ontology file");
        if (file == null) {
            return ontologyURI;
        }
        // Add a mapping from the ontology to the file.  If the user wants the ontology
        // to be editable, then they should have the option to copy the file into the
        // base folder.
//        owlEditorKit.getOWLModelManager().add(ontologyURI, file.toURI());
        return file.toURI();

        //"<font color=\"gray\">Cause: " + e.getMessage() + " (" + e.getClass().getSimpleName() + ")</font><br><br>"
    }
}

