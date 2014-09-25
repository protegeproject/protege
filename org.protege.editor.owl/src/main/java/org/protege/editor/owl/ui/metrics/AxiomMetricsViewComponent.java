package org.protege.editor.owl.ui.metrics;

import org.protege.editor.owl.ui.view.AbstractActiveOntologyViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 30-Jul-2007<br><br>
 */
public class AxiomMetricsViewComponent extends AbstractActiveOntologyViewComponent {

    private MetricsPanel metricsPanel;

    protected void initialiseOntologyView() throws Exception {
        metricsPanel = new MetricsPanel(getOWLEditorKit());
        setLayout(new BorderLayout());
        add(metricsPanel);
    }

    protected void disposeOntologyView() {
        // do nothing
    }

    protected void updateView(OWLOntology activeOntology) throws Exception {
        metricsPanel.updateView(activeOntology);
    }
}
