package org.protege.editor.owl.ui.view.ontology;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.owlxml.renderer.OWLXMLRenderer;

import java.io.Writer;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Mar-2007<br><br>
 */
public class OWLXMLRenderingViewComponent extends AbstractOntologyRenderingViewComponent {


    protected void renderOntology(OWLOntology ontology, Writer writer) throws Exception {
        OWLXMLRenderer renderer = new OWLXMLRenderer();
        renderer.render(ontology, writer);
    }
}
