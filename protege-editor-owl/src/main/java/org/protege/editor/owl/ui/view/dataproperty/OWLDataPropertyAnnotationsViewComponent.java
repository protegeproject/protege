package org.protege.editor.owl.ui.view.dataproperty;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.protege.editor.owl.ui.frame.OWLAnnotationsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLDataProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDataPropertyAnnotationsViewComponent extends AbstractOWLDataPropertyViewComponent {

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


    protected OWLDataProperty updateView(OWLDataProperty property) {
        list.setRootObject(property == null ? null : property.getIRI());
        return property;
    }
}
