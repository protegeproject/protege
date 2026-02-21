package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.prefix.PrefixedNameRenderer;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.ShortFormProvider;

/**
 * Author: Damien Goutte-Gattat<br>
 * German BioImaging e.V.<br>
 * Date: Feb 21, 2026

 * damien@gerbi-gmb.de

 * A short form provider that attempts to condense a IRI using the
 * known IRI prefixes and prefix names, and falls back to another
 * provider if that is not possible.
 */
public class PrefixAwareShortFormProvider implements ShortFormProvider {

    private OWLModelManager manager;
    private PrefixedNameRenderer renderer;
    private ShortFormProvider fallback;

    public PrefixAwareShortFormProvider(OWLModelManager manager, ShortFormProvider fallback) {
        this.manager = manager;
        this.fallback = fallback;
    }

    @Override
    public String getShortForm(OWLEntity entity) {
        if (renderer == null) {
            PrefixedNameRenderer.Builder builder = PrefixedNameRenderer.builder();
            builder.withOwlPrefixes().withWellKnownPrefixes();
            if (manager != null) {
                for (OWLOntology ontology : manager.getOntologies()) {
                    OWLDocumentFormat f = ontology.getOWLOntologyManager().getOntologyFormat(ontology);
                    if (f.isPrefixOWLOntologyFormat()) {
                        f.asPrefixOWLOntologyFormat().getPrefixName2PrefixMap().forEach(builder::withPrefix);
                    }
                }
            }
            renderer = builder.build();
        }

        String shortForm = renderer.getPrefixedNameOrElse(entity.getIRI(), null);
        if (shortForm == null) {
            shortForm = fallback.getShortForm(entity);
        }

        return shortForm;
    }

    @Override
    public void dispose() {
    }
}
