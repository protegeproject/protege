package org.protege.editor.owl.ui.view;

import java.io.Writer;

import org.coode.owl.functionalrenderer.OWLFunctionalSyntaxRenderer;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Mar-2007<br><br>
 */
public class OWLFunctionalSyntaxRenderingViewComponent extends AbstractOntologyRenderingViewComponent {

    protected void renderOntology(OWLOntology ontology, Writer writer) throws Exception {
        OWLFunctionalSyntaxRenderer ren = new OWLFunctionalSyntaxRenderer(getOWLModelManager().getOWLOntologyManager());
        ren.render(ontology, writer);
    }
}
