package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeListener;

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
 * Bio-Health Informatics Group<br>
 * Date: 20-Mar-2007<br><br>
 */
public abstract class AbstractActiveOntologyViewComponent extends AbstractOWLViewComponent {

    private OWLModelManagerListener owlModelManagerListener;

    private OWLOntologyChangeListener owlOntologyChangeListener;

    private HierarchyListener hierarchyListener;

    private boolean updateView;


    final protected void initialiseOWLView() throws Exception {
        owlModelManagerListener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                    markForUpdate();
                }
            }
        };
        getOWLModelManager().addListener(owlModelManagerListener);

        owlOntologyChangeListener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
                for (OWLOntologyChange chg : changes) {
                    if (chg.getOntology().equals(getOWLModelManager().getActiveOntology())) {
                        markForUpdate();
                        break;
                    }
                }
            }
        };
        getOWLModelManager().addOntologyChangeListener(owlOntologyChangeListener);

        hierarchyListener = new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent e) {
                markForUpdate();
            }
        };

        addHierarchyListener(hierarchyListener);
        initialiseOntologyView();
    }


    protected abstract void initialiseOntologyView() throws Exception;


    final protected void disposeOWLView() {
        getOWLModelManager().removeListener(owlModelManagerListener);
        getOWLModelManager().removeOntologyChangeListener(owlOntologyChangeListener);
        disposeOntologyView();
    }


    protected abstract void disposeOntologyView();


    private void markForUpdate() {
        if (isShowing() && updateView) {
            updateView = false;
        }
        else {
            updateView = true;
        }
    }


    protected abstract void updateView(OWLOntology activeOntology) throws Exception;
}
