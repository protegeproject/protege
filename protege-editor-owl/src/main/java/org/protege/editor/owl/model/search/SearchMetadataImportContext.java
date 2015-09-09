package org.protege.editor.owl.model.search;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLEditorKitOntologyShortFormProvider;
import org.protege.editor.owl.model.OWLEditorKitShortFormProvider;
import org.protege.editor.owl.ui.renderer.context.OWLObjectRenderingContext;
import org.protege.editor.owl.ui.renderer.styledstring.OWLObjectStyledStringRenderer;
import org.protege.editor.owl.ui.renderer.styledstring.StyledString;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;

import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/10/2012
 */
public class SearchMetadataImportContext {

    private OWLEditorKit editorKit;

    private final OWLObjectStyledStringRenderer styledStringRenderer;

    public SearchMetadataImportContext(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        ShortFormProvider sfp = new OWLEditorKitShortFormProvider(editorKit);
        OntologyIRIShortFormProvider ontsfp = new OWLEditorKitOntologyShortFormProvider(editorKit);
        OWLObjectRenderingContext renderingContext = new OWLObjectRenderingContext(sfp, ontsfp);
        styledStringRenderer = new OWLObjectStyledStringRenderer(renderingContext);
    }

    public OWLEditorKit getEditorKit() {
        return editorKit;
    }

    public Set<OWLOntology> getOntologies() {
        return editorKit.getOWLModelManager().getActiveOntologies();
    }

    public String getRendering(OWLObject object) {
        return editorKit.getOWLModelManager().getRendering(object);
    }

    public StyledString getStyledStringRendering(OWLObject object) {
        return styledStringRenderer.getRendering(object);
    }

}
