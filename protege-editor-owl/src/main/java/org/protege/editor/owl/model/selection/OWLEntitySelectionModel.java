package org.protege.editor.owl.model.selection;

import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 10 Oct 2016
 */
public class OWLEntitySelectionModel {

    private final OWLSelectionModel selectionModel;

    public OWLEntitySelectionModel(@Nonnull OWLSelectionModel selectionModel) {
        this.selectionModel = checkNotNull(selectionModel);
    }

    @Nonnull
    public Optional<OWLClass> getSelectedClass() {
        return getSelection(OWLClass.class);
    }

    @Nonnull
    public Optional<OWLObjectProperty> getSelectedObjectProperty() {
        return getSelection(OWLObjectProperty.class);
    }

    @Nonnull
    public Optional<OWLDataProperty> getSelectedDataProperty() {
        return getSelection(OWLDataProperty.class);
    }

    @Nonnull
    public Optional<OWLAnnotationProperty> getSelectedAnnotationProperty() {
        return getSelection(OWLAnnotationProperty.class);
    }


    @Nonnull
    public Optional<OWLNamedIndividual> getSelectedNamedIndividual() {
        return getSelection(OWLNamedIndividual.class);
    }


    @Nonnull
    public Optional<OWLDatatype> getSelectedDatatype() {
        return getSelection(OWLDatatype.class);
    }


    @SuppressWarnings("unchecked")
    private <T extends OWLEntity> Optional<T> getSelection(Class<T> theClass) {
        OWLObject selObject = selectionModel.getSelectedObject();
        if(selObject == null) {
            // The current selected object is not of the required type
            return Optional.empty();
        }
        if(theClass.isInstance(selObject)) {
            // The entity is the current selected object
            return Optional.of((T) selObject);
        }
        if(OWLEntity.class.isInstance(selObject)) {
            // The current selected object is an entity but not of the required type
            return Optional.empty();
        }
        // Some other object selected that is not an entity.  If the last selected entity
        // is of the required type then that's good to return.
        OWLEntity lastSelectedEntity = selectionModel.getSelectedEntity();
        if(theClass.isInstance(lastSelectedEntity)) {
            return Optional.of((T) lastSelectedEntity);
        }
        else {
            return Optional.empty();
        }
    }


}
