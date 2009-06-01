package org.protege.editor.owl.ui.view.individual;

import org.protege.editor.owl.ui.frame.individual.OWLIndividualFrame;
import org.protege.editor.owl.ui.framelist.CreateNewEquivalentClassAction;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owl.model.OWLNamedIndividual;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLIndividualDescriptionViewComponent extends AbstractOWLIndividualViewComponent {

    private OWLFrameList<OWLNamedIndividual> list;


    public void initialiseIndividualsView() throws Exception {
        list = new OWLFrameList<OWLNamedIndividual>(getOWLEditorKit(), new OWLIndividualFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
        list.addToPopupMenu(new CreateNewEquivalentClassAction<OWLNamedIndividual>());
    }


    public void disposeView() {
        list.dispose();
    }


    protected OWLNamedIndividual updateView(OWLNamedIndividual selelectedIndividual) {
        list.setRootObject(selelectedIndividual);
        return selelectedIndividual;
    }
}
