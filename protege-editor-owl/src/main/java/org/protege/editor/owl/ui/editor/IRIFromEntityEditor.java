package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.FormLabel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.find.EntityFinderField;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 01-Aug-2007<br><br>
 */
public class IRIFromEntityEditor implements OWLObjectEditor<IRI> {

    private OWLEditorKit editorKit;

    private JPanel component = new JPanel(new BorderLayout());

    private EntityFinderField entityFinderField;

    private OWLObjectEditorHandler<IRI> handler;

    private OWLEntity chosenEntity = null;

    private ChangeListener changeListener;


    public IRIFromEntityEditor(OWLEditorKit owlEditorKit) {
        this.editorKit = owlEditorKit;
        JPanel holder = new JPanel(new BorderLayout());
        this.entityFinderField = new EntityFinderField(holder, owlEditorKit);
        component.add(holder, BorderLayout.NORTH);
        FormLabel formLabel = new FormLabel("Entity IRI");
        holder.add(formLabel, BorderLayout.NORTH);
        holder.add(entityFinderField, BorderLayout.SOUTH);
        entityFinderField.setEntityFoundHandler(entity -> {
            chosenEntity = entity;
            entityFinderField.setText(editorKit.getModelManager().getRendering(entity));
            if(changeListener != null) {
                changeListener.stateChanged(new ChangeEvent(entity));
            }
        });
        component.setBorder(BorderFactory.createEmptyBorder(7, 0, 0 , 0));
    }


    public boolean canEdit(Object object) {
        boolean contained = false;
        if(object instanceof IRI) {
            for(OWLOntology ontology : editorKit.getModelManager().getActiveOntologies()) {
                if(ontology.containsEntityInSignature((IRI) object)) {
                    contained = true;
                    break;
                }
            }
        }
        return contained;
    }


    public boolean isPreferred(Object object) {
        return false;
    }

    public OWLObjectEditorHandler<IRI> getHandler() {
        return handler;
    }

    public void setHandler(OWLObjectEditorHandler<IRI> iriEditorHandler) {
        this.handler = iriEditorHandler;
    }

    @Nonnull
    public JComponent getEditorComponent() {
        return component;
    }

    public Set<IRI> getEditedObjects() {
        IRI selObj = getEditedObject();
        if(selObj != null) {
            return Collections.singleton(selObj);
        }
        else {
            return Collections.emptySet();
        }
    }

    @Nullable
    public IRI getEditedObject() {
        return Optional.ofNullable(chosenEntity).map(OWLNamedObject::getIRI).orElse(null);
    }

    public boolean isMultiEditSupported() {
        return false;
    }


    public void clear() {
        setEditedObject(null);
    }


    public boolean setEditedObject(IRI object) {
        if(object == null) {
            chosenEntity = null;
            entityFinderField.setText("");
        }
        else {
            chosenEntity = getEntityFromIri(object);
            entityFinderField.setText(editorKit.getOWLModelManager().getRendering(object));
        }
        return true;
    }

    @Nullable
    private OWLEntity getEntityFromIri(IRI object) {
        final OWLDataFactory df = editorKit.getOWLModelManager().getOWLDataFactory();
        OWLOntology ont = editorKit.getOWLModelManager().getActiveOntology();
        if(ont.containsClassInSignature(object, Imports.INCLUDED)) {
            return df.getOWLClass(object);
        }
        else if(ont.containsObjectPropertyInSignature(object, Imports.INCLUDED)) {
            return df.getOWLObjectProperty(object);
        }
        else if(ont.containsDataPropertyInSignature(object, Imports.INCLUDED)) {
            return df.getOWLDataProperty(object);
        }
        else if(ont.containsIndividualInSignature(object, Imports.INCLUDED)) {
            return df.getOWLNamedIndividual(object);
        }
        else if(ont.containsAnnotationPropertyInSignature(object, Imports.INCLUDED)) {
            return df.getOWLAnnotationProperty(object);
        }
        else if(ont.containsDatatypeInSignature(object, Imports.INCLUDED)) {
            return df.getOWLDatatype(object);
        }
        else {
            return null;
        }
    }


    @Nonnull
    public String getEditorTypeName() {
        return "Entity IRI";
    }


    public JComponent getComponent() {
        return component;
    }


    public void dispose() {
    }


    public void addSelectionListener(ChangeListener listener) {
        this.changeListener = listener;
    }


    public void removeSelectionListener(ChangeListener listener) {
    }
}
