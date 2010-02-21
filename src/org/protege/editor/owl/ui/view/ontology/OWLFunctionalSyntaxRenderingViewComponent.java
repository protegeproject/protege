package org.protege.editor.owl.ui.view.ontology;

import org.coode.owlapi.functionalrenderer.OWLFunctionalSyntaxRenderer;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.Writer;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Mar-2007<br><br>
 */
public class OWLFunctionalSyntaxRenderingViewComponent extends AbstractOntologyRenderingViewComponent {
    private static final long serialVersionUID = -2015539464417134185L;

    protected void renderOntology(OWLOntology ontology, Writer writer) throws Exception {
        OWLFunctionalSyntaxRenderer ren = new OWLFunctionalSyntaxRenderer(getOWLModelManager().getOWLOntologyManager());
        ren.render(ontology, writer);
    }
}
