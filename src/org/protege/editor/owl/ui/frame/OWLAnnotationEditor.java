package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
 * Date: 10-Feb-2007<br><br>
 */
public class OWLAnnotationEditor extends AbstractOWLFrameSectionRowObjectEditor<OWLAnnotation> {


    private OWLEditorKit owlEditorKit;

    private JTabbedPane tabbedPane;

    private JPanel mainPanel;

    private AnnotationURIList uriList;

    private List<OWLAnnotationValueEditor> editors;


    public OWLAnnotationEditor(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        tabbedPane = new JTabbedPane();
        mainPanel = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainPanel.add(splitPane);

        uriList = new AnnotationURIList(owlEditorKit);
        JPanel listHolder = new JPanel(new BorderLayout());
        listHolder.add(new JScrollPane(uriList));
        listHolder.setPreferredSize(new Dimension(200, 300));

        splitPane.setLeftComponent(listHolder);
        splitPane.setRightComponent(tabbedPane);
        splitPane.setBorder(null);
        loadEditors();
    }


    private void loadEditors() {
        editors = new ArrayList<OWLAnnotationValueEditor>();
        editors.add(new OWLConstantEditor(owlEditorKit));
        editors.add(new OWLIndividualAnnotationValueEditor(owlEditorKit));
        editors.add(new OWLAnnonymousIndividualAnnotationValueEditor(owlEditorKit));
        for (OWLAnnotationValueEditor editor : editors) {
            tabbedPane.add(editor.getEditorTypeName(), editor.getComponent());
        }
    }


    private OWLAnnotationValueEditor getSelectedEditor() {
        return editors.get(tabbedPane.getSelectedIndex());
    }


    public void clear() {
        setAnnotation(null);
    }


    public void setAnnotation(OWLAnnotation annotation) {
        uriList.rebuildAnnotationURIList();
        int tabIndex = -1;
        if (annotation != null) {
            uriList.setSelectedURI(annotation.getAnnotationURI());
            for (int i = 0; i < editors.size(); i++) {
                OWLAnnotationValueEditor editor = editors.get(i);
                if (editor.canEdit(annotation.getAnnotationValue())) {
                    editor.setEditedObject(annotation.getAnnotationValue());
                    if (tabIndex == -1) {
                        tabIndex = i;
                    }
                }
                else {
                    editor.setEditedObject(null);
                }
            }
        }
        else {
            uriList.setSelectedURI(OWLRDFVocabulary.RDFS_COMMENT.getURI());
            for (int i = 0; i < editors.size(); i++) {
                OWLAnnotationValueEditor editor = editors.get(i);
                editor.setEditedObject(null);
            }
        }
        tabbedPane.setSelectedIndex(tabIndex == -1 ? 0 : tabIndex);
    }


    public OWLAnnotation getAnnotation() {
        URI uri = uriList.getSelectedURI();
        OWLAnnotationValueEditor editor = getSelectedEditor();
        Object obj = editor.getEditedObject();
        if (obj instanceof OWLConstant) {
            OWLDataFactory dataFactory = owlEditorKit.getOWLModelManager().getOWLDataFactory();
            return dataFactory.getOWLConstantAnnotation(uri, (OWLConstant) obj);
        }
        else if (obj instanceof OWLIndividual) {
            OWLDataFactory dataFactory = owlEditorKit.getOWLModelManager().getOWLDataFactory();
            return dataFactory.getOWLObjectAnnotation(uri, (OWLIndividual) obj);
        }
        else {
            OWLDataFactory dataFactory = owlEditorKit.getOWLModelManager().getOWLDataFactory();
            return dataFactory.getOWLConstantAnnotation(uri, dataFactory.getOWLUntypedConstant(obj.toString()));
        }
    }


    public JComponent getEditorComponent() {
        return mainPanel;
    }


    public JComponent getInlineEditorComponent() {
        return getEditorComponent();
    }


    /**
     * Gets the object that has been edited.
     * @return The edited object
     */
    public OWLAnnotation getEditedObject() {
        return getAnnotation();
    }


    public void dispose() {

    }
}
