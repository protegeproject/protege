package org.protege.editor.owl.ui.view.datatype;

import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.owl.ui.frame.OWLAnnotationsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
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

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 5, 2009<br><br>
 */
public class OWLDataTypeAnnotationsViewComponent extends AbstractOWLDataTypeViewComponent {

        private OWLFrameList<OWLAnnotationSubject> list;


    public void initialiseView() throws Exception {
    	boolean read_only = ((TabbedWorkspace) getWorkspace()).isReadOnly(this.getView().getPlugin());
        list = new OWLFrameList<>(getOWLEditorKit(), new OWLAnnotationsFrame(getOWLEditorKit()), read_only);
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
    }


    public void disposeView() {
        list.dispose();
    }


    protected OWLDatatype updateView(OWLDatatype datatype) {
        list.setRootObject(datatype == null ? null : datatype.getIRI());
        return datatype;
    }
}
