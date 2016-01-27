package org.protege.editor.owl.ui.view.datatype;

import org.protege.editor.owl.ui.frame.datatype.OWLDatatypeDescriptionFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLDatatype;

import javax.swing.*;
import java.awt.*;
/*
* Copyright (C) 2007, University of Manchester
*
*
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
