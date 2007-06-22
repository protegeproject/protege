package org.protege.editor.owl.ui.metrics;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeListener;
import org.semanticweb.owl.util.DLExpressivityChecker;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
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
 * Medical Informatics Group<br>
 * Date: 03-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DLMetricsViewComponent extends AbstractOWLViewComponent {

    private static final Logger logger = Logger.getLogger(DLMetricsViewComponent.class);


    private boolean changed;

    private OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
        public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
            handleChanges(changes);
        }
    };

    private OWLModelManagerListener modelManagerListener = new OWLModelManagerListener() {
        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                changed = true;
                refresh();
            }
        }
    };

    private HierarchyListener hierarchyListener = new HierarchyListener() {
        public void hierarchyChanged(HierarchyEvent e) {
            changed = true;
            refresh();
        }
    };

    private DLNamePanel namePanel;


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        DLExpressivityChecker expressivityChecker = new DLExpressivityChecker(getOWLModelManager().getActiveOntologies());
        namePanel = new DLNamePanel();
        namePanel.setBorder(ComponentFactory.createTitledBorder("DL Expressivity"));
        add(namePanel, BorderLayout.NORTH);
        DLNameKeyPanel keyPanel = new DLNameKeyPanel();
        keyPanel.setBorder(ComponentFactory.createTitledBorder("Symbol key"));
        add(keyPanel, BorderLayout.CENTER);
        setName();
        getOWLModelManager().addOntologyChangeListener(listener);
        getOWLModelManager().addListener(modelManagerListener);
        addHierarchyListener(hierarchyListener);
    }


    protected void disposeOWLView() {
        getOWLModelManager().removeOntologyChangeListener(listener);
        getOWLModelManager().removeListener(modelManagerListener);
        removeHierarchyListener(hierarchyListener);
    }


    private void handleChanges(List<? extends OWLOntologyChange> changes) {
        for (OWLOntologyChange change : changes) {
            if (change.isAxiomChange()) {
                if (change.getAxiom().isLogicalAxiom()) {
                    changed = true;
                    refresh();
                    break;
                }
            }
        }
    }


    private void setName() {
        try {
            DLExpressivityChecker checker = new DLExpressivityChecker(getOWLModelManager().getActiveOntologies());
            namePanel.setConstructs(checker.getConstructs());
        }
        catch (OWLException e) {
            logger.error(e);
        }
    }


    private void refresh() {
        if (isShowing()) {
            if (changed) {
                setName();
                changed = false;
            }
        }
        else {
            changed = true;
        }
    }
}
