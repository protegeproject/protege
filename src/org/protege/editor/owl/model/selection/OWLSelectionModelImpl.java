package org.protege.editor.owl.model.selection;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.semanticweb.owl.model.*;

import java.util.ArrayList;
import java.util.List;


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

    private OWLObject selectedObject;

    private OWLEntity lastSelectedEntity;

    private OWLClass lastSelectedClass;

    private OWLDataProperty lastSelectedDataProperty;

    private OWLObjectProperty lastSelectedObjectProperty;

    private OWLIndividual lastSelectedIndividual;

    private OWLAxiom lastSelectedAxiom;


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


    public OWLObject getSelectedObject() {
        return selectedObject;
    }


    public void setSelectedObject(OWLObject object) {
        if (object == null) {
            if (selectedObject != null) {
                updateSelectedObject(null);
            }
        }
        else {
            if (selectedObject == null) {
                updateSelectedObject(object);
            }
            else if (!selectedObject.equals(object)) {
                updateSelectedObject(object);
            }
        }
    }


    private void updateSelectedObject(OWLObject selObj) {
        selectedObject = selObj;
        updateLastSelection();
        fireSelectionChanged();
    }


    public OWLEntity getSelectedEntity() {
        return lastSelectedEntity;
    }


    private void fireSelectionChanged() {
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
        setSelectedObject(entity);
    }


    public void setSelectedAxiom(OWLAxiom axiom) {
        setSelectedObject(axiom);
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
                        fireSelectionChanged();
                    }
                }
            }


            public void visit(OWLObjectProperty property) {
                if (lastSelectedObjectProperty != null) {
                    if (lastSelectedObjectProperty.equals(entity)) {
                        lastSelectedObjectProperty = null;
                        fireSelectionChanged();
                    }
                }
            }


            public void visit(OWLDataProperty property) {
                if (lastSelectedDataProperty != null) {
                    if (lastSelectedDataProperty.equals(entity)) {
                        lastSelectedDataProperty = null;
                        fireSelectionChanged();
                    }
                }
            }


            public void visit(OWLIndividual individual) {
                if (lastSelectedIndividual != null) {
                    if (lastSelectedIndividual.equals(entity)) {
                        lastSelectedIndividual = null;
                        fireSelectionChanged();
                    }
                }
            }


            public void visit(OWLDataType dataType) {
            }
        });
        if (lastSelectedEntity != null && entity.equals(lastSelectedEntity)) {
            lastSelectedEntity = null;
            fireSelectionChanged();
        }
    }


    private void updateLastSelection() {

        if (selectedObject == null) {
            return;
        }
        if (selectedObject instanceof OWLEntity) {
            lastSelectedEntity = (OWLEntity)selectedObject;
            lastSelectedEntity.accept(new OWLEntityVisitor() {
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
            lastSelectedAxiom = null; // unlikely we will want the axiom selection to still be valid
        }
        else if (selectedObject instanceof OWLAxiom){
            lastSelectedAxiom = (OWLAxiom)selectedObject;
        }
    }


    public OWLClass getLastSelectedClass() {
        return lastSelectedClass;
    }


    public OWLObjectProperty getLastSelectedObjectProperty() {
        return lastSelectedObjectProperty;
    }


    public OWLDataProperty getLastSelectedDataProperty() {
        return lastSelectedDataProperty;
    }


    public OWLIndividual getLastSelectedIndividual() {
        return lastSelectedIndividual;
    }


    public OWLAxiom getLastSelectedAxiom() {
        return lastSelectedAxiom;
    }
}
