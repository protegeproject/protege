package org.protege.editor.owl.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.ontology.OWLOntologyFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.metrics.MetricsPanel;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
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
 * Date: 29-Oct-2007<br><br>
 *
 * A panel that displays information about an ontology.  Annotations,
 * inferred axioms, metrics and the like.
 */
public class OWLOntologyDisplayPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -807382490474755622L;

    private OWLEditorKit owlEditorKit;

    private JLabel ontologyURILabel;

    private OWLOntologyFrame ontologyFrame;

    private OWLFrameList<OWLOntology> frameList;

    private MetricsPanel metricsPanel;


    public OWLOntologyDisplayPanel(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        createUI();
        setOntology(owlEditorKit.getModelManager().getActiveOntology());
    }

    private void createUI() {
        setLayout(new BorderLayout(7, 7));
        ontologyURILabel = new JLabel();
        add(ontologyURILabel, BorderLayout.NORTH);
        ontologyFrame = new OWLOntologyFrame(owlEditorKit);
        ontologyFrame.setRootObject(null);
        frameList = new OWLFrameList<>(owlEditorKit, ontologyFrame);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.85);
        add(splitPane);
        splitPane.setBorder(null);
        splitPane.setLeftComponent(new JScrollPane(frameList));
        metricsPanel = new MetricsPanel(owlEditorKit);
        splitPane.setRightComponent(metricsPanel);
    }

    public void setOntology(OWLOntology ontology) {
        frameList.setRootObject(ontology);
        try {
            metricsPanel.updateView(ontology);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
