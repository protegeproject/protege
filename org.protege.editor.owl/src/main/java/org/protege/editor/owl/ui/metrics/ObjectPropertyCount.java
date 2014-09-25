package org.protege.editor.owl.ui.metrics;

/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Feb-2007<br><br>
 */
public class ObjectPropertyCount extends AbstractIntegerMetric {

    public ObjectPropertyCount() {
        super("Object properties");
    }


    protected int getIntMetric() {
        return getOWLModelManager().getActiveOntology().getObjectPropertiesInSignature().size();
    }
}
