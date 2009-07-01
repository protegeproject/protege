package org.protege.editor.owl.model;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.model.inference.OWLReasonerExceptionHandler;
import org.semanticweb.owlapi.inference.OWLReasonerException;

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
 * Bio-Health Informatics Group<br>
 * Date: 26-Nov-2007<br><br>
 */
public class UIReasonerExceptionHandler implements OWLReasonerExceptionHandler {


    private OWLWorkspace workspace;


    public UIReasonerExceptionHandler(OWLWorkspace workspace) {
        this.workspace = workspace;
    }


    public void handle(OWLReasonerException e) {
        ProtegeApplication.getErrorLog().logError(e);
        Throwable cause = e;
        while(cause.getCause() != null) {
            cause = cause.getCause();
        }
        String msg = cause.getClass().getSimpleName() + ": " + cause.getMessage();
        JOptionPane.showMessageDialog(workspace, msg, "Reasoner Error", JOptionPane.ERROR_MESSAGE);
    }
}
