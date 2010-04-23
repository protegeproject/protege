package org.protege.editor.owl.ui.renderer;

import org.semanticweb.owlapi.model.IRI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 2, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A simple renderer that renderers an entity using its URI fragment
 * if it exists, and if not, renders the full URI.
 */
public class OWLEntityRendererImpl extends AbstractOWLEntityRenderer {


    public void initialise() {
        // do nothing
    }


    public String render(IRI iri) {
        try {
            String rendering = iri.getFragment();
            if (rendering == null) {
                // Get last bit of path
                String path = iri.toURI().getPath();
                if (path == null) {
                    return iri.toQuotedString();
                }
                return iri.toURI().getPath().substring(path.lastIndexOf("/") + 1);
            }
            return RenderingEscapeUtils.getEscapedRendering(rendering);
        }
        catch (Exception e) {
            return "<Error! " + e.getMessage() + ">";
        }
    }


    protected void disposeRenderer() {
        // do nothing
    }
}
