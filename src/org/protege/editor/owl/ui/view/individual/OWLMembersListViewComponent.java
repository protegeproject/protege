package org.protege.editor.owl.ui.view.individual;

import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owlapi.model.*;

import java.util.Collections;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 14, 2008<br><br>
 *
 * Only shows the members of the currently selected class
 */
public class OWLMembersListViewComponent extends OWLIndividualListViewComponent{

    /**
     * 
     */
    private static final long serialVersionUID = -6015526995379146198L;
    private OWLSelectionModelListener l = new OWLSelectionModelListener(){
        public void selectionChanged() throws Exception {
            if (getOWLWorkspace().getOWLSelectionModel().getSelectedObject() instanceof OWLClass){
                refill();
            }
        }
    };


    @Override
    public void initialiseIndividualsView() throws Exception {
        super.initialiseIndividualsView();
        getOWLWorkspace().getOWLSelectionModel().addListener(l);
    }


    protected void refill() {
        individualsInList.clear();
        OWLClass cls = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
        if (cls != null){
            for (OWLIndividual ind : cls.getIndividuals(getOntologies())){
                if (!ind.isAnonymous()){
                    individualsInList.add(ind.asOWLNamedIndividual());
                }
            }
        }
        reset();
    }


    protected List<OWLOntologyChange> dofurtherCreateSteps(OWLIndividual newIndividual) {
        OWLClass cls = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
        if (cls != null){
            OWLAxiom typeAxiom = getOWLModelManager().getOWLDataFactory().getOWLClassAssertionAxiom(cls, newIndividual);
            OWLOntologyChange change = new AddAxiom(getOWLModelManager().getActiveOntology(), typeAxiom);
            return Collections.singletonList(change);
        }
        return dofurtherCreateSteps(newIndividual);
    }


    @Override
    public void disposeView() {
        getOWLWorkspace().getOWLSelectionModel().removeListener(l);
        super.disposeView();
    }
}
