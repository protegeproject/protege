package org.protege.editor.owl.ui.view.dataproperty;

import org.protege.editor.owl.ui.frame.OWLAnnotationsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDataPropertyAnnotationsViewComponent extends AbstractOWLDataPropertyViewComponent {

    private OWLFrameList<OWLEntity> list;


    public void initialiseView() throws Exception {
        list = new OWLFrameList<OWLEntity>(getOWLEditorKit(), new OWLAnnotationsFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
    }


    public void disposeView() {
        list.dispose();
    }


    protected OWLDataProperty updateView(OWLDataProperty property) {
        list.setRootObject(property);
        return property;
    }
}
