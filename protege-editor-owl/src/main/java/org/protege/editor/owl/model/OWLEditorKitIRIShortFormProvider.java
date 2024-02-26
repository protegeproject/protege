package org.protege.editor.owl.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.Nonnull;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.IRIShortFormProvider;
import org.semanticweb.owlapi.util.SimpleIRIShortFormProvider;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 7 Nov 2016
 */
public class OWLEditorKitIRIShortFormProvider implements IRIShortFormProvider {

    private final OWLEditorKit editorKit;

    private final SimpleIRIShortFormProvider delegateIRIShortFormProvider;

    public OWLEditorKitIRIShortFormProvider(@Nonnull OWLEditorKit editorKit,
                                            @Nonnull SimpleIRIShortFormProvider delegateIRIShortFormProvider) {
        this.editorKit = checkNotNull(editorKit);
        this.delegateIRIShortFormProvider = checkNotNull(delegateIRIShortFormProvider);
    }

    @Nonnull
    @Override
    public String getShortForm(@Nonnull IRI iri) {
        OWLModelManager manager = editorKit.getOWLModelManager();
        Set<OWLEntity> entityWithIri = manager.getActiveOntology().getEntitiesInSignature(iri);
        if(entityWithIri.isEmpty()) {
            return delegateIRIShortFormProvider.getShortForm(iri);
        }
        else {
            return manager.getRendering(entityWithIri.iterator().next());
        }
    }
}
