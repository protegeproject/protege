package org.protege.editor.owl.ui.view.annotationproperty;

/*
* Copyright (C) 2007, University of Manchester
*
*
*/
import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.protege.editor.owl.ui.frame.OWLAnnotationsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 4, 2009<br><br>
 */
public class OWLAnnotationPropertyAnnotationsViewComponent extends AbstractOWLAnnotationPropertyViewComponent {

        private OWLFrameList<OWLAnnotationSubject> list;


    public void initialiseView() throws Exception {
        list = new OWLFrameList<>(getOWLEditorKit(), new OWLAnnotationsFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane(list);
        sp.getVerticalScrollBar().setUnitIncrement(20);
        add(sp);
    }


    public void disposeView() {
        list.dispose();
    }


    protected OWLAnnotationProperty updateView(OWLAnnotationProperty property) {
        list.setRootObject(property == null ? null : property.getIRI());
        return property;
    }
}
