package org.protege.editor.owl.ui.editor;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.frame.individual.OWLClassAssertionAxiomTypeFrameSection;
import org.protege.editor.owl.ui.frame.individual.OWLIndividualPropertyAssertionsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owl.model.OWLAnonymousIndividual;
import org.semanticweb.owl.model.OWLIndividual;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.Set;

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

        frameList = new OWLFrameList<OWLAnonymousIndividual>(owlEditorKit, frame);

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


    public JComponent getEditorComponent() {
        return mainComponent;
    }


    public OWLAnonymousIndividual getEditedObject() {
        return frameList.getRootObject();
    }


    public Set<OWLAnonymousIndividual> getEditedObjects() {
        return Collections.singleton(getEditedObject());
    }


    public boolean setEditedObject(OWLAnonymousIndividual object) {
        if (object == null) {
            // @@TODO what about anonymous ontologies?
            String id = editorKit.getModelManager().getActiveOntology().getOntologyID().getDefaultDocumentIRI() + "#genid" + System.nanoTime();
            object = editorKit.getModelManager().getOWLDataFactory().getOWLAnonymousIndividual(id);
        }
        frameList.setRootObject(object);
        if (object != null) {
            mainComponent.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            annotationValueLabel.setIcon(OWLIcons.getIcon("individual.png"));
            annotationValueLabel.setText(editorKit.getModelManager().getRendering((OWLIndividual) object));
        }
        else {
            annotationValueLabel.setIcon(null);
            annotationValueLabel.setText("");
        }
        return true;
    }


    public boolean isMultiEditSupported() {
        return false;
    }


    public void clear() {
        setEditedObject(null);
    }


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


    class OWLAnonymousIndividualPropertyAssertionsFrame extends OWLIndividualPropertyAssertionsFrame{

        public OWLAnonymousIndividualPropertyAssertionsFrame(OWLEditorKit owlEditorKit) {
            super(owlEditorKit);
            addSection(new OWLClassAssertionAxiomTypeFrameSection(owlEditorKit, this), 0);
// @@TODO OWLAnonymousIndividual should implement OWLAnnotationSubject
//            addSection(new OWLAnnotationFrameSection(owlEditorKit, this), 0);
        }
    }
}
