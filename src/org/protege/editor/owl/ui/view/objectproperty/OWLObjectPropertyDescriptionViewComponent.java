package org.protege.editor.owl.ui.view.objectproperty;

import org.protege.editor.owl.ui.frame.objectproperty.OWLObjectPropertyDescriptionFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList2;
import org.semanticweb.owl.model.OWLObjectProperty;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLObjectPropertyDescriptionViewComponent extends AbstractOWLObjectPropertyViewComponent {

    private OWLFrameList2<OWLObjectProperty> list;


    public void initialiseView() throws Exception {
        list = new OWLFrameList2<OWLObjectProperty>(getOWLEditorKit(),
                                                    new OWLObjectPropertyDescriptionFrame(getOWLEditorKit()));
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
