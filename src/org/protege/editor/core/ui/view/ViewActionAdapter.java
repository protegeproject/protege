package org.protege.editor.core.ui.view;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
