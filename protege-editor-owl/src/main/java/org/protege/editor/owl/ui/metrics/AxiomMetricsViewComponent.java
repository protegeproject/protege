package org.protege.editor.owl.ui.metrics;

/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */
import java.awt.BorderLayout;

import org.protege.editor.owl.ui.view.AbstractActiveOntologyViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 30-Jul-2007<br><br>
 */
public class AxiomMetricsViewComponent extends AbstractActiveOntologyViewComponent {

    private static final Logger logger = LoggerFactory.getLogger(AxiomMetricsViewComponent.class);

    private MetricsPanel metricsPanel;

    protected void initialiseOntologyView() throws Exception {
        metricsPanel = new MetricsPanel(getOWLEditorKit());
        setLayout(new BorderLayout());
        add(metricsPanel);
    }

    protected void disposeOntologyView() {
        try {
            metricsPanel.dispose();
        } catch (Exception e) {
            logger.error("An error occurred whilst disposing of the metrics panel", e);
        }
    }

    protected void updateView(OWLOntology activeOntology) throws Exception {
        metricsPanel.updateView(activeOntology);
    }
}
