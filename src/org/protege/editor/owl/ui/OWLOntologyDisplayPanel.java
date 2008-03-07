package org.protege.editor.owl.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.OWLOntologyFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList2;
import org.protege.editor.owl.ui.metrics.MetricsPanel;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
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
 * Date: 29-Oct-2007<br><br>
 *
 * A panel that displays information about an ontology.  Annotations,
 * inferred axioms, metrics and the like.
 */
public class OWLOntologyDisplayPanel extends JPanel {

    private OWLEditorKit owlEditorKit;

    private JLabel ontologyURILabel;

    private OWLOntologyFrame ontologyFrame;

    private OWLFrameList2<OWLOntology> frameList;

    private MetricsPanel metricsPanel;


    public OWLOntologyDisplayPanel(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        createUI();
        setOntology(owlEditorKit.getOWLModelManager().getActiveOntology());
    }

    private void createUI() {
        setLayout(new BorderLayout(7, 7));
        ontologyURILabel = new JLabel();
        add(ontologyURILabel, BorderLayout.NORTH);
        ontologyFrame = new OWLOntologyFrame(owlEditorKit);
        ontologyFrame.setRootObject(null);
        frameList = new OWLFrameList2<OWLOntology>(owlEditorKit, ontologyFrame);
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
