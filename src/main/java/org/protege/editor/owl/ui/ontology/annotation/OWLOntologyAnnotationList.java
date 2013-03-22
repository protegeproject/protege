package org.protege.editor.owl.ui.ontology.annotation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OntologyAnnotationContainer;
import org.protege.editor.owl.ui.list.AbstractAnnotationsList;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;

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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 1, 2009<br><br>
 */
public class OWLOntologyAnnotationList extends AbstractAnnotationsList<OntologyAnnotationContainer> {
    private static final long serialVersionUID = -639600900445260658L;


    public OWLOntologyAnnotationList(OWLEditorKit eKit) {
        super(eKit);
    }


    protected List<OWLOntologyChange> getAddChanges(OWLAnnotation annot) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.add(new AddOntologyAnnotation(getRoot().getOntology(), annot));
        return changes;
    }


    protected List<OWLOntologyChange> getReplaceChanges(OWLAnnotation oldAnnotation, OWLAnnotation newAnnotation) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.add(new RemoveOntologyAnnotation(getRoot().getOntology(), oldAnnotation));
        changes.add(new AddOntologyAnnotation(getRoot().getOntology(), newAnnotation));
        return changes;
    }


    protected List<OWLOntologyChange> getDeleteChanges(OWLAnnotation annot) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.add(new RemoveOntologyAnnotation(getRoot().getOntology(), annot));
        return changes;
    }


    protected void handleOntologyChanges(List<? extends OWLOntologyChange> changes) {
        for (OWLOntologyChange change : changes){
            if (change instanceof AddOntologyAnnotation ||
                change instanceof RemoveOntologyAnnotation){
                if (change.getOntology().equals(getRoot().getOntology())){
                    refresh();
                    return;
                }
            }
        }
    }
}
