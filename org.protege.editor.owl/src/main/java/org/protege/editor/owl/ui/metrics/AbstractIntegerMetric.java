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
public abstract class AbstractIntegerMetric extends Metric {

    private String metric;


    public AbstractIntegerMetric(String name) {
        super(name);
    }


    public String getMetric() {
        return metric;
    }


    public void updateMetric() {
        metric = Integer.toString(getIntMetric());
    }


    protected abstract int getIntMetric();
}
