package org.protege.editor.owl.ui.view.ontology;

import org.coode.owl.rdf.rdfxml.RDFXMLRenderer;
import org.semanticweb.owl.model.OWLOntology;

import java.io.Writer;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Mar-2007<br><br>
 */
public class RDFXMLRenderingViewComponent extends AbstractOntologyRenderingViewComponent {

    protected void renderOntology(OWLOntology ontology, Writer writer) {
        RDFXMLRenderer renderer = new RDFXMLRenderer(getOWLModelManager().getOWLOntologyManager(),
                                                     getOWLModelManager().getActiveOntology(),
                                                     writer);
        renderer.render();
    }
}
