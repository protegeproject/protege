package org.protege.editor.owl.ui.view.dataproperty;

import org.protege.editor.owl.ui.frame.dataproperty.OWLDataPropertyDomainsAndRangesFrame;
import org.protege.editor.owl.ui.framelist.CreateNewEquivalentClassAction;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLDataProperty;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDataPropertyDomainsAndRangesViewComponent extends AbstractOWLDataPropertyViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -4570654746910757818L;
    private OWLFrameList<OWLDataProperty> list;


    public void initialiseView() throws Exception {
        list = new OWLFrameList<OWLDataProperty>(getOWLEditorKit(),
                                                  new OWLDataPropertyDomainsAndRangesFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
        list.addToPopupMenu(new CreateNewEquivalentClassAction<OWLDataProperty>());
    }


    protected OWLDataProperty updateView(OWLDataProperty property) {
        list.setRootObject(property);
        return property;
    }


    public void disposeView() {
        super.disposeView();
        list.dispose();
    }
}
