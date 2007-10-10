package org.protege.editor.owl.ui.view;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.protege.editor.owl.ui.frame.OWLAnnotationsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList2;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObjectProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 04-Feb-2007<br><br>
 */
public class OWLObjectPropertyAnnotationsViewComponent extends AbstractOWLObjectPropertyViewComponent {

    private OWLFrameList2<OWLEntity> list;


    public void initialiseView() throws Exception {
        list = new OWLFrameList2<OWLEntity>(getOWLEditorKit(), new OWLAnnotationsFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
    }


    public void disposeView() {
        list.dispose();
    }


    protected OWLObjectProperty updateView(OWLObjectProperty property) {
        list.setRootObject(property);
        return property;
    }
}
