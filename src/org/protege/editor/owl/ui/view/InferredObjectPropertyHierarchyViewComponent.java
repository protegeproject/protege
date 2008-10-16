package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.property.InferredObjectPropertyHierarchyProvider;
import org.semanticweb.owl.model.OWLObjectProperty;

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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 16, 2008<br><br>
 */
public class InferredObjectPropertyHierarchyViewComponent extends OWLObjectPropertyHierarchyViewComponent {

    private OWLModelManagerListener l = new OWLModelManagerListener(){

        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.getType() == EventType.ACTIVE_ONTOLOGY_CHANGED ||
                event.getType() == EventType.ONTOLOGY_CLASSIFIED ||
                event.getType() == EventType.REASONER_CHANGED){
                getHierarchyProvider().setOntologies(getOWLModelManager().getActiveOntologies());
            }
        }
    };


    protected void performExtraInitialisation() throws Exception {
        getOWLModelManager().addListener(l);
        getTree().setBackground(new Color(255, 255, 215));
    }


    public void disposeView() {
        getOWLModelManager().removeListener(l);
        super.disposeView();
    }


    protected OWLObjectHierarchyProvider<OWLObjectProperty> getHierarchyProvider() {
        OWLObjectHierarchyProvider<OWLObjectProperty> hp = getOWLModelManager().get(InferredObjectPropertyHierarchyProvider.ID);
        if (hp == null){
            hp = new InferredObjectPropertyHierarchyProvider(getOWLModelManager());
            hp.setOntologies(getOWLModelManager().getActiveOntologies());
            getOWLModelManager().put(InferredObjectPropertyHierarchyProvider.ID, hp);
        }
        return hp;
    }
}
