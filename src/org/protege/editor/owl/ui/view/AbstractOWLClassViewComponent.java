package org.protege.editor.owl.ui.view;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLEntity;
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
 * Date: Mar 21, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An <code>OWLView</code> that displays information about classes
 */
public abstract class AbstractOWLClassViewComponent extends AbstractOWLSelectionViewComponent {

    final public void initialiseView() throws Exception {
        initialiseClassView();
    }


    public abstract void initialiseClassView() throws Exception;


    final protected OWLEntity updateView() {
        OWLClass cls = updateView(getSelectedOWLClass());
        if (cls != null) {
            updateRegisteredActions();
        }
        else {
            disableRegisteredActions();
        }
        return cls;
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
    protected abstract OWLClass updateView(OWLClass selectedClass);


    /**
     * A convenience method.
     * Gets the selected <code>OWLClass</code> from the main
     * <code>OWLSelectionModel</code>.  (This is not the class
     * that the view is displaying)
     * @return The selected <code>OWLClass</code>, or
     *         <code>null</code> if no class is selected.
     */
    protected OWLClass getSelectedOWLClass() {
        return getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
    }


    protected boolean isOWLClassView() {
        return true;
    }
}
