package org.protege.editor.owl.ui.view.individual;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.protege.editor.owl.ui.frame.OWLAnnotationsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLNamedIndividual;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 04-Feb-2007<br><br>
 */
public class OWLIndividualAnnotationsViewComponent extends AbstractOWLIndividualViewComponent {

    private static final long serialVersionUID = -3036939007144710932L;

    private OWLFrameList<OWLAnnotationSubject> list;


    public void initialiseIndividualsView() throws Exception {
        list = new OWLFrameList<>(getOWLEditorKit(), new OWLAnnotationsFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane(list);
        sp.getVerticalScrollBar().setUnitIncrement(20);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(sp);
    }


    public void disposeView() {
        list.dispose();
    }


    public OWLNamedIndividual updateView(OWLNamedIndividual selectedIndividual) {
        list.setRootObject(selectedIndividual == null ? null : selectedIndividual.getIRI());
        return selectedIndividual;
    }
}
