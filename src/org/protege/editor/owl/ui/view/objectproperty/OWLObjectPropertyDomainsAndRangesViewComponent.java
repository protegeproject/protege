package org.protege.editor.owl.ui.view.objectproperty;

import org.protege.editor.owl.ui.frame.objectproperty.OWLObjectPropertyDomainsAndRangesFrame;
import org.protege.editor.owl.ui.framelist.CreateNewEquivalentClassAction;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLObjectPropertyDomainsAndRangesViewComponent extends AbstractOWLObjectPropertyViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -4918417723540993141L;
    private OWLFrameList<OWLObjectProperty> list;


    public void initialiseView() throws Exception {
        list = new OWLFrameList<OWLObjectProperty>(getOWLEditorKit(),
                                                    new OWLObjectPropertyDomainsAndRangesFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
        list.addToPopupMenu(new CreateNewEquivalentClassAction<OWLObjectProperty>());
    }


    public void disposeView() {
        list.dispose();
    }


    protected OWLObjectProperty updateView(OWLObjectProperty property) {
        list.setRootObject(property);
        return property;
    }
}
