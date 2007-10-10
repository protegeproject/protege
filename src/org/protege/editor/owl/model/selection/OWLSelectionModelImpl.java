package org.protege.editor.owl.model.selection;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;


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
