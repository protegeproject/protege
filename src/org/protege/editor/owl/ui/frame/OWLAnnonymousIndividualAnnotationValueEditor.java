package org.protege.editor.owl.ui.frame;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.framelist.OWLFrameList2;
import org.semanticweb.owl.model.OWLIndividual;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
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
public class OWLAnnonymousIndividualAnnotationValueEditor implements OWLAnnotationValueEditor {

    private OWLFrameList2<OWLIndividual> frameList;

    private JComponent mainComponent;

    private OWLEditorKit editorKit;


    public OWLAnnonymousIndividualAnnotationValueEditor(OWLEditorKit owlEditorKit) {
        editorKit = owlEditorKit;
        frameList = new OWLFrameList2<OWLIndividual>(owlEditorKit,
                                                     new OWLIndividualPropertyAssertionsFrame(owlEditorKit));
        mainComponent = new JPanel(new BorderLayout());
        mainComponent.add(new JScrollPane(frameList));
    }


    public boolean canEdit(Object object) {
        return object instanceof OWLIndividual;
    }


    public Object getEditedObject() {
        return frameList.getRootObject();
    }


    public void setEditedObject(Object object) {
        if (object == null) {
            URI uri = URI.create(editorKit.getOWLModelManager().getActiveOntology().getURI() + "#genid" + System.nanoTime());
            object = editorKit.getOWLModelManager().getOWLDataFactory().getOWLAnonymousIndividual(uri);
        }
        frameList.setRootObject((OWLIndividual) object);
        if (object != null) {
            mainComponent.setBorder(ComponentFactory.createTitledBorder(editorKit.getOWLModelManager().getRendering((OWLIndividual) object)));
        }
    }


    public String getEditorTypeName() {
        return "Property values";
    }


    public JComponent getComponent() {
        return mainComponent;
    }
}
