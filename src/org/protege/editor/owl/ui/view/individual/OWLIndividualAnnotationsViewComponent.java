package org.protege.editor.owl.ui.view.individual;

import org.protege.editor.owl.ui.frame.OWLAnnotationsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 04-Feb-2007<br><br>
 */
public class OWLIndividualAnnotationsViewComponent extends AbstractOWLIndividualViewComponent {

    private OWLFrameList<OWLEntity> list;


    public void initialiseIndividualsView() throws Exception {
        list = new OWLFrameList<OWLEntity>(getOWLEditorKit(), new OWLAnnotationsFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
    }


    public void disposeView() {
        list.dispose();
    }


    protected OWLNamedIndividual updateView(OWLNamedIndividual selelectedIndividual) {
        list.setRootObject(selelectedIndividual);
        return selelectedIndividual;
    }
}
