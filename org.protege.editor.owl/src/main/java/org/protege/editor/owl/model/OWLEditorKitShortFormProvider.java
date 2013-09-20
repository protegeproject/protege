package org.protege.editor.owl.model;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.ShortFormProvider;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 27/09/2012
 * <p>
 * A ShortFormProvider that is based on the rendering settings in an {@link OWLEditorKit}.
 * </p>
 */
public class OWLEditorKitShortFormProvider implements ShortFormProvider {

    private OWLEditorKit editorKit;

    public OWLEditorKitShortFormProvider(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
    }

    public String getShortForm(OWLEntity entity) {
        return editorKit.getOWLModelManager().getRendering(entity);
    }

    public void dispose() {
    }
}
