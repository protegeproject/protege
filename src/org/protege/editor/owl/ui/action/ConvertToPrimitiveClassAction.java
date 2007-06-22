package org.protege.editor.owl.ui.action;

import org.semanticweb.owl.model.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
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
 * Date: 24-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ConvertToPrimitiveClassAction extends SelectedOWLClassAction {


    public void actionPerformed(ActionEvent e) {
        // TODO: Factor this out into some kind of API util
        OWLClass selCls = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
        if (selCls == null) {
            return;
        }
        OWLDataFactory dataFactory = getOWLModelManager().getOWLDataFactory();
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            for (OWLEquivalentClassesAxiom ax : ont.getEquivalentClassesAxioms(selCls)) {
                changes.add(new RemoveAxiom(ont, ax));
                for (OWLDescription desc : ax.getDescriptions()) {
                    if (!desc.equals(selCls)) {
                        if (desc instanceof OWLObjectIntersectionOf) {
                            for (OWLDescription op : ((OWLObjectIntersectionOf) desc).getOperands()) {
                                changes.add(new AddAxiom(ont, dataFactory.getOWLSubClassAxiom(selCls, op)));
                            }
                        }
                        else {
                            changes.add(new AddAxiom(ont, dataFactory.getOWLSubClassAxiom(selCls, desc)));
                        }
                    }
                }
            }
        }
        getOWLModelManager().applyChanges(changes);
    }


    protected void updateState() {
        OWLEntity selEnt = getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
        setEnabled(selEnt instanceof OWLClass);
    }


    protected void initialiseAction() throws Exception {
    }


    public void dispose() {
    }
}
