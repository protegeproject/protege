package org.protege.editor.owl.ui.frame.editor;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLEntitySelectorPanel;
import org.semanticweb.owl.model.IRI;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.util.Collections;
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
public class IRIAnnotationValueEditor implements OWLAnnotationValueEditor<IRI> {

    private OWLEntitySelectorPanel entitySelectorPanel;

    private OWLEditorKit eKit;


    public IRIAnnotationValueEditor(OWLEditorKit owlEditorKit) {
        this.eKit = owlEditorKit;
        entitySelectorPanel = new OWLEntitySelectorPanel(owlEditorKit, false);
    }


    public boolean canEdit(Object object) {
        return object instanceof OWLIndividual;
    }


    public boolean isPreferred(Object object) {
        return object instanceof OWLIndividual && !((OWLIndividual) object).isAnonymous();
    }


    public IRI getEditedObject() {
        return entitySelectorPanel.getSelectedObject().getIRI();
    }


    public void setEditedObject(IRI object) {
        if (object != null){
        final OWLDataFactory df = eKit.getOWLModelManager().getOWLDataFactory();
        for (OWLOntology ont : eKit.getOWLModelManager().getActiveOntologies()){
            if (ont.containsClassReference(object.toURI())){
                entitySelectorPanel.setSelection(Collections.singleton(df.getOWLClass(object.toURI())));
                break;
            }
            else if (ont.containsObjectPropertyReference(object.toURI())){
                entitySelectorPanel.setSelection(Collections.singleton(df.getOWLObjectProperty(object.toURI())));
                break;
            }
            else if (ont.containsDataPropertyReference(object.toURI())){
                entitySelectorPanel.setSelection(Collections.singleton(df.getOWLDataProperty(object.toURI())));
                break;
            }
            else if (ont.containsIndividualReference(object.toURI())){
                entitySelectorPanel.setSelection(Collections.singleton(df.getOWLNamedIndividual(object.toURI())));
                break;
            }
        }
        }
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
