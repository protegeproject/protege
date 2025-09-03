package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.ui.find.OWLEntityFindPanel;
import org.protege.editor.owl.ui.view.Findable;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.FocusManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 28-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class FindInViewAction extends ProtegeOWLAction {

    private PropertyChangeListener listener;


    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {
        // We actually don't need to do anything here,
        // because this will be handled by virtue of the
        // fact that the ancestor of the focused component
        // is a Findable
        Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();
        if(focusOwner == null) {
            return;
        }
        Findable<OWLEntity> f = (Findable<OWLEntity>) SwingUtilities.getAncestorOfClass(Findable.class, focusOwner);
        if(f == null) {
            return;
        }
        OWLEntity foundEntity = OWLEntityFindPanel.showDialog(focusOwner, getOWLEditorKit(), f);
        if (foundEntity == null) {
            return;
        }
        f.show(foundEntity);
    }

    private void handleFind() {

    }


    public void initialise() throws Exception {
        FocusManager.getCurrentManager().addPropertyChangeListener(listener = evt -> {
            if (evt.getPropertyName().equals("focusOwner")) {
                Component c = (Component) evt.getNewValue();
                Findable<?> f = (Findable<?>) SwingUtilities.getAncestorOfClass(Findable.class, c);
                setEnabled(f != null);
            }
        });
    }


    public void dispose() {
        FocusManager.getCurrentManager().removePropertyChangeListener(listener);
    }
}
