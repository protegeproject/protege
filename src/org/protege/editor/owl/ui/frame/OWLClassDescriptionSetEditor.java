package org.protege.editor.owl.ui.frame;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLDescriptionChecker;
import org.protege.editor.owl.ui.selector.OWLClassSelectorPanel;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
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
 * Date: 26-Feb-2007<br><br>
 */
public class OWLClassDescriptionSetEditor extends AbstractOWLFrameSectionRowObjectEditor<Set<OWLDescription>> {

    private static final Logger logger = Logger.getLogger(OWLClassDescriptionSetEditor.class);


    private OWLEditorKit owlEditorKit;

    private OWLClassSelectorPanel classSelectorPanel;

    private JComponent editorComponent;

    private ExpressionEditor<OWLDescription> expressionEditor;

    private JTabbedPane tabbedPane;


    public OWLClassDescriptionSetEditor(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
    }


    private void createEditor() {
        editorComponent = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane();
        editorComponent.add(tabbedPane);
        classSelectorPanel = new OWLClassSelectorPanel(owlEditorKit);
        tabbedPane.add("Class hierarchy", classSelectorPanel);
        OWLDescriptionChecker checker = new OWLDescriptionChecker(owlEditorKit);
        expressionEditor = new ExpressionEditor<OWLDescription>(owlEditorKit, checker);
        JPanel holderPanel = new JPanel(new BorderLayout());
        holderPanel.add(expressionEditor);
        holderPanel.setPreferredSize(new Dimension(500, 400));
        holderPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        tabbedPane.add("Expression editor", holderPanel);
    }


    public JComponent getEditorComponent() {
        if (editorComponent == null) {
            createEditor();
        }
        classSelectorPanel.setSelectedClass(owlEditorKit.getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass());
        return editorComponent;
    }


    public void clear() {
        if (editorComponent != null) {
            expressionEditor.setText("");
        }
    }


    public Set<OWLDescription> getEditedObject() {
        if (tabbedPane.getSelectedComponent().equals(classSelectorPanel)) {
            return classSelectorPanel.getSelectedClasses();
        }
        else {
            try {
                if (!expressionEditor.isWellFormed()) {
                    return null;
                }
                Set<OWLDescription> clses = new HashSet<OWLDescription>();
                clses.add(expressionEditor.createObject());
                return clses;
            }
            catch (OWLException e) {
                logger.error(e);
            }
        }
        return null;
    }


    public void dispose() {
        classSelectorPanel.dispose();
    }
}
