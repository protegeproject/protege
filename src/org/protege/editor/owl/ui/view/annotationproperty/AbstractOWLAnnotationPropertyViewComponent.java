package org.protege.editor.owl.ui.view.annotationproperty;

import org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLObject;
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
 * Date: Apr 15, 2009<br><br>
 */
public abstract class AbstractOWLAnnotationPropertyViewComponent extends AbstractOWLSelectionViewComponent {

    protected boolean isOWLAnnotationPropertyView() {
        return true;
    }


    protected OWLObject updateView() {
        OWLAnnotationProperty selProp = updateView(getOWLWorkspace().getOWLSelectionModel().getLastSelectedAnnotationProperty());
        if (selProp != null) {
            updateRegisteredActions();
        }
        else {
            disableRegisteredActions();
        }
        return selProp;
    }


    protected abstract OWLAnnotationProperty updateView(OWLAnnotationProperty property);
}
