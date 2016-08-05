package org.protege.editor.owl.model.selection;


import org.protege.editor.owl.model.util.OWLAxiomInstance;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 21, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLSelectionModelImpl implements OWLSelectionModel {

    private final Logger logger = LoggerFactory.getLogger(OWLSelectionModelImpl.class);


    private final List<OWLSelectionModelListener> listeners = new ArrayList<>();

    private OWLObject selectedObject;

    private OWLEntity lastSelectedEntity;

    private OWLClass lastSelectedClass;

    private OWLDataProperty lastSelectedDataProperty;

    private OWLObjectProperty lastSelectedObjectProperty;

    private OWLAnnotationProperty lastSelectedAnnotationProperty;

    private OWLNamedIndividual lastSelectedIndividual;

    private OWLDatatype lastSelectedDatatype;

    private OWLAxiomInstance lastSelectedAxiomInstance;


    private final OWLEntityVisitor updateVisitor = new OWLEntityVisitor() {
        public void visit(@Nonnull OWLClass cls) {
            lastSelectedClass = cls;
        }


        public void visit(@Nonnull OWLObjectProperty property) {
            lastSelectedObjectProperty = property;
        }


        public void visit(@Nonnull OWLDataProperty property) {
            lastSelectedDataProperty = property;
        }


        public void visit(@Nonnull OWLAnnotationProperty owlAnnotationProperty) {
            lastSelectedAnnotationProperty = owlAnnotationProperty;
        }


        public void visit(@Nonnull OWLNamedIndividual individual) {
            lastSelectedIndividual = individual;
        }


        public void visit(@Nonnull OWLDatatype dataType) {
            lastSelectedDatatype = dataType;
        }
    };

    private final OWLEntityVisitor clearVisitor = new OWLEntityVisitor() {
        public void visit(@Nonnull OWLClass cls) {
            if (lastSelectedClass != null) {
                if (lastSelectedClass.equals(cls)) {
                    lastSelectedClass = null;
                    fireSelectionChanged();
                }
            }
        }

        public void visit(@Nonnull OWLObjectProperty property) {
            if (lastSelectedObjectProperty != null) {
                if (lastSelectedObjectProperty.equals(property)) {
                    lastSelectedObjectProperty = null;
                    fireSelectionChanged();
                }
            }
        }

        public void visit(@Nonnull OWLDataProperty property) {
            if (lastSelectedDataProperty != null) {
                if (lastSelectedDataProperty.equals(property)) {
                    lastSelectedDataProperty = null;
                    fireSelectionChanged();
                }
            }
        }

        public void visit(@Nonnull OWLAnnotationProperty property) {
            if (lastSelectedAnnotationProperty != null) {
                if (lastSelectedAnnotationProperty.equals(property)) {
                    lastSelectedAnnotationProperty = null;
                    fireSelectionChanged();
                }
            }
        }

        public void visit(@Nonnull OWLNamedIndividual individual) {
            if (lastSelectedIndividual != null) {
                if (lastSelectedIndividual.equals(individual)) {
                    lastSelectedIndividual = null;
                    fireSelectionChanged();
                }
            }
        }

        public void visit(@Nonnull OWLDatatype dataType) {
            if (lastSelectedDatatype != null) {
                if (lastSelectedDatatype.equals(dataType)) {
                    lastSelectedDatatype = null;
                    fireSelectionChanged();
                }
            }
        }
    };

    public OWLSelectionModelImpl() {
    }


    @Override
    public void addListener(@Nonnull OWLSelectionModelListener listener) {
        listeners.add(checkNotNull(listener));
    }

    @Override
    public void removeListener(@Nonnull OWLSelectionModelListener listener) {
        listeners.remove(checkNotNull(listener));
    }


    @Nullable
    public OWLObject getSelectedObject() {
        return selectedObject;
    }


    @Override
    public void setSelectedObject(@Nullable OWLObject object) {
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


    @Override
    public OWLEntity getSelectedEntity() {
        return lastSelectedEntity;
    }


    private void fireSelectionChanged() {
        for (OWLSelectionModelListener listener : new ArrayList<>(listeners)) {
            try {
                listener.selectionChanged();
            }
            catch (Exception e) {
                logger.warn("A selection model listener threw an error whilst handling a selection changed event: {}", e);
            }
        }
    }

    @Override
    public void setSelectedEntity(@Nullable OWLEntity entity) {
        setSelectedObject(entity);
    }

    @Override
    public void setSelectedAxiom(@Nonnull OWLAxiomInstance axiomInstance) {
        lastSelectedAxiomInstance = axiomInstance;
        setSelectedObject(axiomInstance.getAxiom());
    }


    @Override
    public void clearLastSelectedEntity(@Nonnull OWLEntity entity) {
        entity.accept(clearVisitor);
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

            lastSelectedEntity.accept(updateVisitor);
            lastSelectedAxiomInstance = null; // unlikely we will want the axiom selection to still be valid
        }
    }


    @Override
    public OWLClass getLastSelectedClass() {
        return lastSelectedClass;
    }

    @Override
    public OWLObjectProperty getLastSelectedObjectProperty() {
        return lastSelectedObjectProperty;
    }


    @Override
    public OWLDataProperty getLastSelectedDataProperty() {
        return lastSelectedDataProperty;
    }

    @Override
    public OWLAnnotationProperty getLastSelectedAnnotationProperty() {
        return lastSelectedAnnotationProperty;
    }

    @Override
    public OWLNamedIndividual getLastSelectedIndividual() {
        return lastSelectedIndividual;
    }

    @Override
    public OWLDatatype getLastSelectedDatatype() {
        return lastSelectedDatatype;
    }

    @Override
    public OWLAxiomInstance getLastSelectedAxiomInstance() {
        return lastSelectedAxiomInstance;
    }
}
