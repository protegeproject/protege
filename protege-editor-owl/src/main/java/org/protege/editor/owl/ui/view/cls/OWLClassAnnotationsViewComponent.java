package org.protege.editor.owl.ui.view.cls;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.protege.editor.owl.ui.frame.OWLAnnotationsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLClass;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 04-Feb-2007<br><br>
 */
public class OWLClassAnnotationsViewComponent extends AbstractOWLClassViewComponent {

        private OWLFrameList<OWLAnnotationSubject> list;


    public void initialiseClassView() throws Exception {
        list = new OWLFrameList<>(getOWLEditorKit(), new OWLAnnotationsFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        JScrollPane comp = new JScrollPane(list);
        comp.getVerticalScrollBar().setUnitIncrement(20);
        comp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(comp);
    }


    public void disposeView() {
        list.dispose();
    }


    /**
     * This method is called to request that the view is updated with
     * the specified class.
     * @param selectedClass The class that the view should be updated with.  Note
     *                      that this may be <code>null</code>, which indicates that the view should
     *                      be cleared
     * @return The actual class that the view is displaying after it has been updated
     *         (may be <code>null</code>)
     */
    protected OWLClass updateView(OWLClass selectedClass) {
        list.setRootObject(selectedClass == null ? null : selectedClass.getIRI());
        return selectedClass;
    }
}
