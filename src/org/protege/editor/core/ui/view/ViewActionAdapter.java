package org.protege.editor.core.ui.view;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ViewActionAdapter extends ViewAction {

    private DisposableAction action;


    public ViewActionAdapter(DisposableAction action) {
        this.action = action;
        Object [] keys = action.getKeys();
        for (int i = 0; i < keys.length; i++) {
            putValue((String) keys[i], action.getValue((String) keys[i]));
        }
        setEnabled(action.isEnabled());
        action.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if ("enabled".equals(evt.getPropertyName())) {
                    setEnabled((Boolean) evt.getNewValue());
                }
                else {
                    // Pass it on!
                    ViewActionAdapter.this.action.putValue(evt.getPropertyName(), evt.getNewValue());
                }
            }
        });
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        // Just delegate
        action.actionPerformed(e);
    }


    /**
     * This method is called at the end of a plugin
     * life cycle, when the plugin needs to be removed
     * from the system.  Plugins should remove any listeners
     * that they setup and perform other cleanup, so that
     * the plugin can be garbage collected.
     */
    public void dispose() {
        action.dispose();
    }


    /**
     * The initialise method is called at the start of a
     * plugin instance life cycle.
     * This method is called to give the plugin a chance
     * to intitialise itself.  All plugin initialisation
     * should be done in this method rather than the plugin
     * constructor, since the initialisation might need to
     * occur at a point after plugin instance creation, and
     * a each plugin must have a zero argument constructor.
     */
    public void initialise() throws Exception {
        // Do nothing here
    }
}
