package org.protege.editor.owl.ui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.FocusManager;
import javax.swing.SwingUtilities;

import org.protege.editor.owl.ui.search.SearchDialogPanel;
import org.protege.editor.owl.ui.view.Findable;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 28-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class FindAction extends ProtegeOWLAction {

    private PropertyChangeListener listener;


    public void actionPerformed(ActionEvent e) {
        // We actually don't need to do anything here,
        // because this will be handled by virtue of the
        // fact that the ancestor of the focused component
        // is a Findable
    }


    public void initialise() throws Exception {
        FocusManager.getCurrentManager().addPropertyChangeListener(listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("focusOwner")) {
                    Component c = (Component) evt.getNewValue();
                    Findable f = (Findable) SwingUtilities.getAncestorOfClass(Findable.class, c);
                    setEnabled(f != null);
                }
            }
        });
    }


    public void dispose() {
        FocusManager.getCurrentManager().removePropertyChangeListener(listener);
    }
}
