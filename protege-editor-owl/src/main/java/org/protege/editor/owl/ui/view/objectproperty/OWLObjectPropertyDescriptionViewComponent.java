package org.protege.editor.owl.ui.view.objectproperty;

import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.owl.ui.frame.objectproperty.OWLObjectPropertyDescriptionFrame;
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
public class OWLObjectPropertyDescriptionViewComponent extends AbstractOWLObjectPropertyViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -2293216220246399531L;
    private OWLFrameList<OWLObjectProperty> list;


    public void initialiseView() throws Exception {
    	if (((TabbedWorkspace) getWorkspace()).isReadOnly(this.getView().getPlugin())) {
    		list = new OWLFrameList<>(getOWLEditorKit(),
    				new OWLObjectPropertyDescriptionFrame(getOWLEditorKit()), true);

    	} else {
    		list = new OWLFrameList<>(getOWLEditorKit(),
    				new OWLObjectPropertyDescriptionFrame(getOWLEditorKit()));
    	}
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
    }


    public void disposeView() {
        list.dispose();
    }


    protected OWLObjectProperty updateView(OWLObjectProperty property) {
        list.setRootObject(property);
        return property;
    }
}
