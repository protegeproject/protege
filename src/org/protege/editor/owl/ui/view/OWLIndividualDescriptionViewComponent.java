package org.protege.editor.owl.ui.view;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.protege.editor.owl.ui.frame.OWLIndividualFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList2;
import org.semanticweb.owl.model.OWLIndividual;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLIndividualDescriptionViewComponent extends AbstractOWLIndividualViewComponent {

    private OWLFrameList2<OWLIndividual> list;


    public void initialiseIndividualsView() throws Exception {
        list = new OWLFrameList2<OWLIndividual>(getOWLEditorKit(), new OWLIndividualFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
    }


    public void disposeView() {
        list.dispose();
    }


    protected OWLIndividual updateView(OWLIndividual selelectedIndividual) {
        list.setRootObject(selelectedIndividual);
        return selelectedIndividual;
    }
}
