package org.protege.editor.owl.model;

import org.protege.editor.owl.model.inference.OWLReasonerExceptionHandler;
import org.slf4j.LoggerFactory;

import javax.swing.*;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
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


    public void handle(Throwable e) {
        Throwable cause = e;
        while(cause.getCause() != null) {
            cause = cause.getCause();
        }
        String msg = cause.getClass().getSimpleName() + ": " + cause.getMessage();
        JOptionPane.showMessageDialog(workspace, msg, "An error occurred during reasoning", JOptionPane.ERROR_MESSAGE);
    }
}
