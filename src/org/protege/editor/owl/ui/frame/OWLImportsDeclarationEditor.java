package org.protege.editor.owl.ui.frame;

import java.awt.Frame;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportParameters;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportTypePage;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportVerifier;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;


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
