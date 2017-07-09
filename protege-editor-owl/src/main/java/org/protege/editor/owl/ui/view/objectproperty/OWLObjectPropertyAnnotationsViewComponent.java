package org.protege.editor.owl.ui.view.objectproperty;

import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.owl.ui.frame.OWLAnnotationsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 04-Feb-2007<br><br>
 */
public class OWLObjectPropertyAnnotationsViewComponent extends AbstractOWLObjectPropertyViewComponent {

    private static final long serialVersionUID = 8491580078087562290L;

    private OWLFrameList<OWLAnnotationSubject> list;


    public void initialiseView() throws Exception {
    	boolean read_only = ((TabbedWorkspace) getWorkspace()).isReadOnly(this.getView().getPlugin());
        list = new OWLFrameList<>(getOWLEditorKit(), new OWLAnnotationsFrame(getOWLEditorKit()), read_only);
        setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane(list);
        sp.getVerticalScrollBar().setUnitIncrement(20);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(sp);
    }


    public void disposeView() {
        list.dispose();
    }


    protected OWLObjectProperty updateView(OWLObjectProperty property) {
        list.setRootObject(property == null ? null : property.getIRI());
        return property;
    }
}
