package org.protege.editor.owl.ui.action;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.model.refactor.ontology.OntologyMerger;
import org.protege.editor.owl.ui.ontology.wizard.merge.MergeOntologiesWizard;
import org.semanticweb.owl.model.OWLOntology;

import java.awt.event.ActionEvent;
import java.util.Set;
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
 * Date: 02-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MergeOntologiesAction extends ProtegeOWLAction {

    private static final Logger logger = Logger.getLogger(MergeOntologiesAction.class);


    public void actionPerformed(ActionEvent e) {
        MergeOntologiesWizard wizard = new MergeOntologiesWizard(getOWLEditorKit());
        if (wizard.showModalDialog() == Wizard.FINISH_RETURN_CODE) {
            Set<OWLOntology> ontologies = wizard.getOntologiesToMerge();
            OWLOntology ont = wizard.getOntologyToMergeInto();
            OntologyMerger merger = new OntologyMerger(getOWLModelManager().getOWLOntologyManager(), ontologies, ont);
            merger.mergeOntologies();
        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
