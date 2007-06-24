package org.protege.editor.owl.ui.view;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.protege.editor.owl.ui.frame.OWLIndividualPropertyAssertionsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList2;
import org.protege.editor.owl.ui.framelist.OWLFrameListRenderer;
import org.semanticweb.owl.model.OWLIndividual;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 30-Jan-2007<br><br>
 */
public class OWLIndividualPropertyAssertionsViewComponent extends AbstractOWLIndividualViewComponent {

    private OWLFrameList2<OWLIndividual> list;


    public void initialiseIndividualsView() throws Exception {
        list = new OWLFrameList2<OWLIndividual>(getOWLEditorKit(),
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


    protected OWLIndividual updateView(OWLIndividual selelectedIndividual) {
        list.setRootObject(selelectedIndividual);
        return selelectedIndividual;
    }
}
