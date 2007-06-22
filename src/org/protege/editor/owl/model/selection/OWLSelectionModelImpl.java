package org.protege.editor.owl.model.selection;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.semanticweb.owl.model.*;

import java.util.ArrayList;
import java.util.List;
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
 */
public class OWLSelectionModelImpl implements OWLSelectionModel {

    private static final Logger logger = Logger.getLogger(OWLSelectionModelImpl.class);


    private List<OWLSelectionModelListener> listeners;

    private OWLEntity selectedEntity;

    private OWLClass lastSelectedClass;

    private OWLDataProperty lastSelectedDataProperty;

    private OWLObjectProperty lastSelectedObjectProperty;

    private OWLIndividual lastSelectedIndividual;


    public OWLSelectionModelImpl() {
        listeners = new ArrayList<OWLSelectionModelListener>();
        logger.setLevel(Level.WARN);
    }


    public void addListener(OWLSelectionModelListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener must not be null!");
        }
        listeners.add(listener);
    }


    public void removeListener(OWLSelectionModelListener listener) {
        listeners.remove(listener);
    }


    public OWLEntity getSelectedEntity() {
        return selectedEntity;
    }


    private void fireSelectedEntityChanged() {
        for (OWLSelectionModelListener listener : new ArrayList<OWLSelectionModelListener>(listeners)) {
            try {
                listener.selectionChanged();
            }
            catch (Exception e) {
                logger.warn("BAD LISTENER: (" + listener.getClass().getSimpleName() + ") ");
                ProtegeApplication.getErrorLog().handleError(Thread.currentThread(), e);
            }
        }
    }


    public void setSelectedEntity(OWLEntity entity) {
        if (entity == null) {
            if (selectedEntity != null) {
                selectedEntity = null;
                updateLastSelection();
                fireSelectedEntityChanged();
            }
        }
        else {
            if (selectedEntity == null) {
                selectedEntity = entity;
                updateLastSelection();
                fireSelectedEntityChanged();
            }
            else if (!selectedEntity.equals(entity)) {
                selectedEntity = entity;
                updateLastSelection();
                fireSelectedEntityChanged();
            }
        }
    }


    public void clearLastSelectedEntity(final OWLEntity entity) {
        if (entity == null) {
            return;
        }
        entity.accept(new OWLEntityVisitor() {
            public void visit(OWLClass cls) {
                if (lastSelectedClass != null) {
                    if (lastSelectedClass.equals(entity)) {
                        lastSelectedClass = null;
                        fireSelectedEntityChanged();
                    }
                }
            }


            public void visit(OWLObjectProperty property) {
                if (lastSelectedObjectProperty != null) {
                    if (lastSelectedObjectProperty.equals(entity)) {
                        lastSelectedObjectProperty = null;
                        fireSelectedEntityChanged();
                    }
                }
            }


            public void visit(OWLDataProperty property) {
                if (lastSelectedDataProperty != null) {
                    if (lastSelectedDataProperty.equals(entity)) {
                        lastSelectedDataProperty = null;
                        fireSelectedEntityChanged();
                    }
                }
            }


            public void visit(OWLIndividual individual) {
                if (lastSelectedIndividual != null) {
                    if (lastSelectedIndividual.equals(entity)) {
                        lastSelectedIndividual = null;
                        fireSelectedEntityChanged();
                    }
                }
            }


            public void visit(OWLDataType dataType) {
            }
        });
        if (selectedEntity != null && entity.equals(selectedEntity)) {
            selectedEntity = null;
            fireSelectedEntityChanged();
        }
    }


    private void updateLastSelection() {
        if (selectedEntity == null) {
            return;
        }
        selectedEntity.accept(new OWLEntityVisitor() {
            public void visit(OWLClass cls) {
                lastSelectedClass = cls;
            }


            public void visit(OWLObjectProperty property) {
                lastSelectedObjectProperty = property;
            }


            public void visit(OWLDataProperty property) {
                lastSelectedDataProperty = property;
            }


            public void visit(OWLIndividual individual) {
                lastSelectedIndividual = individual;
            }


            public void visit(OWLDataType dataType) {
            }
        });
    }


    /**
     * Gets the first selected class.
     * @return The selected <code>OWLClass</code>, or <code>null</code>
     *         if a class is not selected.
     */
    public OWLClass getLastSelectedClass() {
        return lastSelectedClass;
    }


    /**
     * Gets the first selected property
     * @return The selected <code>OWLProperty</code>, or <code>null</code>
     *         if there is no selected property.
     */
    public OWLObjectProperty getLastSelectedObjectProperty() {
        return lastSelectedObjectProperty;
    }


    /**
     * Gets the first selected property
     * @return The selected <code>OWLProperty</code>, or <code>null</code>
     *         if there is no selected property.
     */
    public OWLDataProperty getLastSelectedDataProperty() {
        return lastSelectedDataProperty;
    }


    /**
     * Gets the first selected individual.
     * @return The selected individual, or <code>null</code> if
     *         there is no selected individual.
     */
    public OWLIndividual getLastSelectedIndividual() {
        return lastSelectedIndividual;
    }
}
