package org.protege.editor.owl.ui.action;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLObjectDuplicator;

import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * Date: 19-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DuplicateSelectedClassAction extends SelectedOWLClassAction {

    private static final Logger logger = Logger.getLogger(DuplicateSelectedClassAction.class);


    protected void initialiseAction() throws Exception {
    }


    public void actionPerformed(ActionEvent e) {
        OWLClass selectedClass = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
        OWLEntityCreationSet<OWLClass> set = getOWLWorkspace().createOWLClass();
        Map<URI, URI> replacementURIMap = new HashMap<URI, URI>();
        replacementURIMap.put(selectedClass.getURI(), set.getOWLEntity().getURI());
        OWLObjectDuplicator dup = new OWLObjectDuplicator(getOWLModelManager().getOWLDataFactory(), replacementURIMap);
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.addAll(set.getOntologyChanges());
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            for (OWLAxiom ax : ont.getAxioms(selectedClass)) {
                if (ax.isLogicalAxiom() && !(ax instanceof OWLDisjointClassesAxiom)) {
                    OWLAxiom duplicatedAxiom = dup.duplicateObject(ax);
                    changes.add(new AddAxiom(ont, duplicatedAxiom));
                }
            }
        }

        getOWLModelManager().applyChanges(changes);
        getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(set.getOWLEntity());
    }
}
