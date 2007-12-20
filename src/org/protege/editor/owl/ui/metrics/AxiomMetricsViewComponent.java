package org.protege.editor.owl.ui.metrics;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.protege.editor.owl.ui.view.Copyable;
import org.protege.editor.owl.ui.view.ChangeListenerMediator;
import org.semanticweb.owl.metrics.*;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.*;
import java.util.List;
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
 * Date: 30-Jul-2007<br><br>
 */
public class AxiomMetricsViewComponent extends AbstractOWLViewComponent {

    private boolean update;

    private MetricsPanel metricsPanel;

    private OWLModelManagerListener listener = new OWLModelManagerListener() {

        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.getType().equals(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                try {
                    metricsPanel.updateView(getOWLEditorKit().getOWLModelManager().getActiveOntology());
                }
                catch (Exception e) {
                    ProtegeApplication.getErrorLog().logError(e);
                }
            }
        }
    };

    private OWLOntologyChangeListener ontologyChangeListener = new OWLOntologyChangeListener() {

        public void ontologiesChanged(List<? extends OWLOntologyChange> list) throws OWLException {
            handleChanges();
        }
    };


    private HierarchyListener hierarchyListener = new HierarchyListener() {

        public void hierarchyChanged(HierarchyEvent e) {
            if (update) {
                metricsPanel.updateView(getOWLEditorKit().getOWLModelManager().getActiveOntology());
            }
        }
    };


    protected void initialiseOWLView() throws Exception {
        metricsPanel = new MetricsPanel(getOWLEditorKit());
        setLayout(new BorderLayout());
        add(metricsPanel);
        getOWLModelManager().addListener(listener);
        getOWLModelManager().addOntologyChangeListener(ontologyChangeListener);
        addHierarchyListener(hierarchyListener);
    }


    private void handleChanges() {
        if (isShowing()) {
            metricsPanel.updateView(getOWLEditorKit().getOWLModelManager().getActiveOntology());
        }
        else {
            update = true;
        }
    }


    protected void disposeOWLView() {
        getOWLModelManager().removeListener(listener);
        getOWLModelManager().removeOntologyChangeListener(ontologyChangeListener);
        removeHierarchyListener(hierarchyListener);
    }
}
