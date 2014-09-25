package org.protege.editor.owl.model.repository;

import java.net.URI;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 18-Oct-2008<br><br>
 */
public class TONESRepository extends ManchesterRepository {


    public TONESRepository() {
        super("TONES", URI.create("http://owl.cs.manchester.ac.uk/repository"));
    }
}
