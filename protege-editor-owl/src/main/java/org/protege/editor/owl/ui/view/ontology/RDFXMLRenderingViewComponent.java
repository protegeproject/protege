package org.protege.editor.owl.ui.view.ontology;

import java.io.Writer;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.rdf.rdfxml.renderer.RDFXMLRenderer;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Mar-2007<br><br>
 */
public class RDFXMLRenderingViewComponent extends AbstractOntologyRenderingViewComponent {


    protected void renderOntology(OWLOntology ontology, Writer writer) throws Exception {
        RDFXMLRenderer renderer = new RDFXMLRenderer(getOWLModelManager().getActiveOntology(),
                                                     writer);
        renderer.render();
    }
}
