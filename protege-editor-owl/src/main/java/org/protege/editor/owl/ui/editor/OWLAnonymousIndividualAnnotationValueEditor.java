package org.protege.editor.owl.ui.editor;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.frame.OWLAnnotationFrameSection;
import org.protege.editor.owl.ui.frame.individual.OWLClassAssertionAxiomTypeFrameSection;
import org.protege.editor.owl.ui.frame.individual.OWLIndividualPropertyAssertionsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.renderer.OWLIndividualIcon;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 01-Aug-2007<br><br>
 */
public class OWLAnonymousIndividualAnnotationValueEditor implements OWLObjectEditor<OWLAnonymousIndividual> {

    private OWLFrameList<OWLAnonymousIndividual> frameList;

    private JComponent mainComponent;

    private OWLEditorKit editorKit;

    private JLabel annotationValueLabel;

    private OWLObjectEditorHandler<OWLAnonymousIndividual> handler;


    public OWLAnonymousIndividualAnnotationValueEditor(OWLEditorKit owlEditorKit) {
        editorKit = owlEditorKit;

        OWLAnonymousIndividualPropertyAssertionsFrame frame = new OWLAnonymousIndividualPropertyAssertionsFrame(owlEditorKit);

        frameList = new OWLFrameList<>(owlEditorKit, frame);

        mainComponent = new JPanel(new BorderLayout(7, 7));
        JScrollPane sp = new JScrollPane(frameList);
        JPanel scrollPaneHolder = new JPanel(new BorderLayout());
        scrollPaneHolder.add(sp);
        scrollPaneHolder.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        mainComponent.add(scrollPaneHolder);
        annotationValueLabel = new JLabel();
        mainComponent.add(annotationValueLabel, BorderLayout.NORTH);
    }


    public boolean canEdit(Object object) {
        return object instanceof OWLIndividual;
    }


    public boolean isPreferred(Object object) {
        return object instanceof OWLIndividual && ((OWLIndividual) object).isAnonymous();
    }


    @Nonnull
    public JComponent getEditorComponent() {
        return mainComponent;
    }


    @Nullable
    public OWLAnonymousIndividual getEditedObject() {
        return frameList.getRootObject();
    }


    public Set<OWLAnonymousIndividual> getEditedObjects() {
        return Collections.singleton(getEditedObject());
    }


    public boolean setEditedObject(OWLAnonymousIndividual object) {
        if (object == null) {
            String id = "genid-" + UUID.randomUUID().toString();
            object = editorKit.getModelManager().getOWLDataFactory().getOWLAnonymousIndividual(id);
        }
        frameList.setRootObject(object);
        mainComponent.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        annotationValueLabel.setIcon(new OWLIndividualIcon());
        annotationValueLabel.setText(editorKit.getModelManager().getRendering(object));
        return true;
    }


    public boolean isMultiEditSupported() {
        return false;
    }


    public void clear() {
        setEditedObject(null);
    }


    @Nonnull
    public String getEditorTypeName() {
        return "Property values";
    }


    public void dispose() {
        frameList.dispose();
    }


    public void setHandler(OWLObjectEditorHandler<OWLAnonymousIndividual> handler) {
        this.handler = handler;
    }


    public OWLObjectEditorHandler<OWLAnonymousIndividual> getHandler() {
        return handler;
    }


    class OWLAnonymousIndividualPropertyAssertionsFrame extends OWLIndividualPropertyAssertionsFrame<OWLAnonymousIndividual>{

        public OWLAnonymousIndividualPropertyAssertionsFrame(OWLEditorKit owlEditorKit) {
            super(owlEditorKit);
            addSection(new OWLClassAssertionAxiomTypeFrameSection(owlEditorKit, this), 0);
            addSection(new OWLAnnotationFrameSection(owlEditorKit, this), 0);
        }
    }
}
