package org.protege.editor.owl.ui.prefix;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.prefix.PrefixedNameRenderer;
import org.protege.editor.owl.ui.renderer.AbstractOWLEntityRenderer;
import org.protege.editor.owl.ui.renderer.prefix.PrefixBasedRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.PrefixManager;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 19 Oct 2017
 */
public class OWLEntityPrefixedNameRenderer extends AbstractOWLEntityRenderer implements PrefixBasedRenderer {

    private PrefixedNameRenderer renderer;

    @Override
    public void initialise() {
        PrefixManager manager = PrefixUtilities.getPrefixOWLOntologyFormat(getOWLModelManager());
        PrefixedNameRenderer.Builder builder = PrefixedNameRenderer.builder();
        manager.getPrefixName2PrefixMap().forEach(builder::withPrefix);
        renderer = builder.withOwlPrefixes().withWellKnownPrefixes().build();
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public boolean configure(OWLEditorKit eKit) {
        throw new RuntimeException("This renderer is not configurable");
    }

    @Override
    public String render(IRI iri) {
        if(renderer == null) {
            throw new RuntimeException("Renderer has not been initialised");
        }
        return renderer.getPrefixedNameOrQuotedIri(iri);
    }

    @Override
    protected void disposeRenderer() {

    }
}
