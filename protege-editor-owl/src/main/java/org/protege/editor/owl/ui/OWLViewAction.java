package org.protege.editor.owl.ui;

import org.protege.editor.core.ui.view.ViewAction;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 3, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class OWLViewAction extends ViewAction {



    public OWLEditorKit getOWLEditorKit() {
        return (OWLEditorKit) getEditorKit();
    }


    public AbstractOWLViewComponent getOWLViewComponent() {
        return (AbstractOWLViewComponent) getView().getViewComponent();
    }
}
