package org.protege.editor.owl.ui.view.datatype;

import org.protege.editor.owl.ui.frame.datatype.OWLDatatypeDescriptionFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLDatatype;

import javax.swing.*;
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
 * Date: Jun 5, 2009<br><br>
 */
public class OWLDataTypeDescriptionViewComponent extends AbstractOWLDataTypeViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 4103047350123901528L;
    private OWLFrameList<OWLDatatype> list;


    public void initialiseView() throws Exception {
        list = new OWLFrameList<OWLDatatype>(getOWLEditorKit(),
                                             new OWLDatatypeDescriptionFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
    }


    protected OWLDatatype updateView(OWLDatatype datatype) {
        list.setRootObject(datatype);
        return datatype;
    }


    public void disposeView() {
        list.dispose();
    }
}
