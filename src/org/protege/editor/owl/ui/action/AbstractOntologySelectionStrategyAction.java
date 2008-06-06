package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.selection.ontologies.OntologySelectionStrategy;

import java.awt.event.ActionEvent;/*
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
 * Date: Jun 6, 2008<br><br>
 */
public abstract class AbstractOntologySelectionStrategyAction extends ProtegeOWLAction {

    private OWLModelManagerListener l = new OWLModelManagerListener(){
        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.getType() == EventType.ONTOLOGY_VISIBILITY_CHANGED){
                AbstractOntologySelectionStrategyAction.this.setEnabled(isNotCurrent());
            }
        }
    };


    private boolean isNotCurrent() {
        return !getOWLModelManager().getActiveOntologiesStrategy().getClass().equals(getStrategy().getClass());
    }


    public void actionPerformed(ActionEvent event) {
        getOWLModelManager().setActiveOntologiesStrategy(getStrategy());
    }


    public void initialise() throws Exception {
        setEnabled(isNotCurrent());
        getOWLModelManager().addListener(l);
    }


    public void dispose() throws Exception {
        getOWLModelManager().removeListener(l);
    }


    protected abstract OntologySelectionStrategy getStrategy();
}
