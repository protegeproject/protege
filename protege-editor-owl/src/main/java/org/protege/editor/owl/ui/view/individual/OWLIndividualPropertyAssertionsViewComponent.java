package org.protege.editor.owl.ui.view.individual;

import org.protege.editor.owl.ui.frame.individual.OWLIndividualPropertyAssertionsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.framelist.OWLFrameListRenderer;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 30-Jan-2007<br><br>
 */
public class OWLIndividualPropertyAssertionsViewComponent extends AbstractOWLIndividualViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -1228370750437540626L;
    private OWLFrameList<OWLIndividual> list;


    public void initialiseIndividualsView() throws Exception {
        list = new OWLFrameList<>(getOWLEditorKit(),
                                                new OWLIndividualPropertyAssertionsFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
        OWLFrameListRenderer renderer = new OWLFrameListRenderer(getOWLEditorKit());
        renderer.setHighlightKeywords(false);
        list.setCellRenderer(renderer);
    }


    public void disposeView() {
        list.dispose();
    }


    public OWLNamedIndividual updateView(OWLNamedIndividual selelectedIndividual) {
        list.setRootObject(selelectedIndividual);
        return selelectedIndividual;
    }
}
