package org.protege.editor.owl.ui.view;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.protege.editor.owl.ui.frame.OWLGeneralClassAxiomsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Apr-2007<br><br>
 */
public class OWLGeneralAxiomsViewComponent extends AbstractActiveOntologyViewComponent {

        private OWLFrameList<OWLOntology> list;


    protected void initialiseOntologyView() throws Exception {
        list = new OWLFrameList<>(getOWLEditorKit(),
                                              new OWLGeneralClassAxiomsFrame(getOWLEditorKit(),
                                                                             getOWLModelManager().getOWLOntologyManager()));
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
        list.setRootObject(getOWLModelManager().getActiveOntology());
    }


    protected void disposeOntologyView() {
        list.dispose();
    }


    protected void updateView(OWLOntology activeOntology) throws Exception {
        list.setRootObject(activeOntology);
    }
}
