package org.protege.editor.owl.model.inference;

import org.semanticweb.owlapi.model.OWLRuntimeException;
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
public class DefaultOWLReasonerExceptionHandler implements OWLReasonerExceptionHandler {


    public void handle(Throwable t) {
        throw new OWLRuntimeException(t);
    }
}
