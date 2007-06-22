package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportParameters;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportTypePage;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportVerifier;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
 * Date: 20-Feb-2007<br><br>
 */
public class OWLImportsDeclarationEditor extends OntologyImportWizard implements OWLFrameSectionRowObjectEditor<OWLImportsDeclaration>, OWLFrameSectionRowObjectEditorHandler<OWLImportsDeclaration> {

    private OWLEditorKit editorKit;


    public OWLImportsDeclarationEditor(OWLEditorKit owlEditorKit) {
        super((Frame) SwingUtilities.getAncestorOfClass(Frame.class, owlEditorKit.getWorkspace()), owlEditorKit);
        editorKit = owlEditorKit;
    }


    public OWLImportsDeclaration getEditedObject() {
        return null;
    }


    public JComponent getEditorComponent() {
        return null;
    }


    public JComponent getInlineEditorComponent() {
        return null;
    }


    public OWLFrameSectionRowObjectEditorHandler<OWLImportsDeclaration> getHandler() {
        return this;
    }


    public void setHandler(OWLFrameSectionRowObjectEditorHandler<OWLImportsDeclaration> handler) {

    }


    public OWLDataFactory getDataFactory() {
        return editorKit.getOWLModelManager().getOWLDataFactory();
    }


    public Set<OWLImportsDeclaration> getEditedObjects() {
        return Collections.singleton(getEditedObject());
    }


    public boolean isMultiEditSupported() {
        return false;
    }


    public void clear() {
        setCurrentPanel(ImportTypePage.ID);
    }


    public void handleEditingFinished(Set<OWLImportsDeclaration> editedObject) {
        try {
            ImportVerifier verifier = getImportVerifier();
            ImportParameters params = verifier.checkImports();
            params.performImportSetup(editorKit);
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            for (URI uri : params.getOntologiesToBeImported()) {
                OWLOntology ont = editorKit.getOWLModelManager().getActiveOntology();
                OWLImportsDeclaration ax = getDataFactory().getOWLImportsDeclarationAxiom(ont, uri);
                changes.add(new AddAxiom(ont, ax));
                editorKit.getOWLModelManager().getOWLOntologyManager().loadOntology(uri);
            }
            editorKit.getOWLModelManager().applyChanges(changes);
        }
        catch (OWLException e) {
            e.printStackTrace();
        }
    }
}
