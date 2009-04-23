package org.protege.editor.owl.ui.view.dataproperty;

import org.protege.editor.owl.ui.frame.dataproperty.OWLDataPropertyDomainsAndRangesFrame;
import org.protege.editor.owl.ui.framelist.CreateNewEquivalentClassAction;
import org.protege.editor.owl.ui.framelist.OWLFrameList2;
import org.semanticweb.owl.model.OWLDataProperty;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDataPropertyDomainsAndRangesViewComponent extends AbstractOWLDataPropertyViewComponent {

    private OWLFrameList2<OWLDataProperty> list;


    public void initialiseView() throws Exception {
        list = new OWLFrameList2<OWLDataProperty>(getOWLEditorKit(),
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
