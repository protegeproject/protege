package org.protege.editor.owl.ui.view.annotationproperty;

import org.protege.editor.owl.ui.frame.annotationproperty.OWLAnnotationPropertyDescriptionFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

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
 * Date: Jun 4, 2009<br><br>
 */
public class OWLAnnotationPropertyDescriptionViewComponent extends AbstractOWLAnnotationPropertyViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -8608154339856654967L;
    private OWLFrameList<OWLAnnotationProperty> list;


    public void initialiseView() throws Exception {
        list = new OWLFrameList<>(getOWLEditorKit(),
                                                  new OWLAnnotationPropertyDescriptionFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
    }


    protected OWLAnnotationProperty updateView(OWLAnnotationProperty property) {
        list.setRootObject(property);
        return property;
    }


    public void disposeView() {
        list.dispose();
    }
}
