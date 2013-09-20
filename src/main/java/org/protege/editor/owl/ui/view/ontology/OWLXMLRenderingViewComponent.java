package org.protege.editor.owl.ui.view.ontology;

import org.coode.owlapi.owlxml.renderer.OWLXMLRenderer;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.Writer;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Mar-2007<br><br>
 */
public class OWLXMLRenderingViewComponent extends AbstractOntologyRenderingViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 4981062527906093700L;

    protected void renderOntology(OWLOntology ontology, Writer writer) throws Exception {
        OWLXMLRenderer renderer = new OWLXMLRenderer(getOWLModelManager().getOWLOntologyManager());
        renderer.render(ontology, writer);
    }
}
