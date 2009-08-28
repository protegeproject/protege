package org.protege.editor.owl.ui.editor;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLEntitySelectorPanel;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;
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
public class IRIAnnotationValueEditor implements OWLObjectEditor<IRI> {

    private OWLEntitySelectorPanel entitySelectorPanel;

    private OWLEditorKit eKit;

    private OWLObjectEditorHandler<IRI> handler;


    public IRIAnnotationValueEditor(OWLEditorKit owlEditorKit) {
        this.eKit = owlEditorKit;
        entitySelectorPanel = new OWLEntitySelectorPanel(owlEditorKit, false);
    }


    public boolean canEdit(Object object) {
        return object instanceof OWLNamedIndividual;
    }


    public boolean isPreferred(Object object) {
        return object instanceof OWLNamedIndividual;
    }


    public void setHandler(OWLObjectEditorHandler<IRI> iriEditorHandler) {
        this.handler = iriEditorHandler;
    }


    public OWLObjectEditorHandler<IRI> getHandler() {
        return handler;
    }


    public JComponent getEditorComponent() {
        return entitySelectorPanel;
    }


    public IRI getEditedObject() {
        final OWLEntity entity = entitySelectorPanel.getSelectedObject();
        return entity != null ? entity.getIRI() : null;
    }


    public Set<IRI> getEditedObjects() {
        IRI selObj = getEditedObject();
        return selObj != null ? Collections.singleton(selObj) : Collections.EMPTY_SET;
    }


    public boolean isMultiEditSupported() {
        return false;
    }


    public void clear() {
        setEditedObject(null);
    }


    public boolean setEditedObject(IRI object) {
        if (object != null){
            final OWLDataFactory df = eKit.getOWLModelManager().getOWLDataFactory();
            for (OWLOntology ont : eKit.getOWLModelManager().getActiveOntologies()){
                if (ont.containsClassReference(object)){
                    entitySelectorPanel.setSelection(Collections.singleton(df.getOWLClass(object.toURI())));
                    break;
                }
                else if (ont.containsObjectPropertyReference(object)){
                    entitySelectorPanel.setSelection(Collections.singleton(df.getOWLObjectProperty(object.toURI())));
                    break;
                }
                else if (ont.containsDataPropertyReference(object)){
                    entitySelectorPanel.setSelection(Collections.singleton(df.getOWLDataProperty(object.toURI())));
                    break;
                }
                else if (ont.containsIndividualReference(object)){
                    entitySelectorPanel.setSelection(Collections.singleton(df.getOWLNamedIndividual(object.toURI())));
                    break;
                }
                else if (ont.containsAnnotationPropertyReference(object)){
                    entitySelectorPanel.setSelection(Collections.singleton(df.getOWLAnnotationProperty(object.toURI())));
                    break;
                }
            }
        }
        else{
            entitySelectorPanel.setSelection((OWLEntity)null);
        }
        return true;
    }


    public String getEditorTypeName() {
        return "Entity IRI";
    }


    public JComponent getComponent() {
        return entitySelectorPanel;
    }


    public void dispose() {
        entitySelectorPanel.dispose();
    }


    public void addSelectionListener(ChangeListener listener) {
        entitySelectorPanel.addSelectionListener(listener);
    }


    public void removeSelectionListener(ChangeListener listener) {
        entitySelectorPanel.addSelectionListener(listener);
    }
}
