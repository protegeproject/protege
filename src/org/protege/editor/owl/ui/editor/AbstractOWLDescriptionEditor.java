package org.protege.editor.owl.ui.editor;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.AxiomType;
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
 * Date: Feb 26, 2009<br><br>
 */
public abstract class AbstractOWLDescriptionEditor implements OWLDescriptionEditor {

    private OWLEditorKit eKit;

    private String label;

    private AxiomType type = null;


    public final void setup(String id, String name, OWLEditorKit eKit) {
        this.eKit = eKit;
        this.label = name;
    }


    public final void setAxiomType(AxiomType type) {
        this.type = type;
    }


    public final String getEditorName() {
        return label;
    }


    protected final OWLEditorKit getOWLEditorKit(){
        return eKit;
    }


    protected final AxiomType getAxiomType(){
        return type;
    }
}
