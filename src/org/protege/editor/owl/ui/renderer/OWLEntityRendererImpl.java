package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLEntity;


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

    public String render(OWLEntity entity) {
        try {
            String rendering = entity.getURI().getFragment();
            if (rendering == null) {
                // Get last bit of path
                String path = entity.getURI().getPath();
                if (path == null) {
                    return entity.getURI().toString();
                }
                return entity.getURI().getPath().substring(path.lastIndexOf("/") + 1);
            }
            return RenderingEscapeUtils.getEscapedRendering(rendering);
        }
        catch (Exception e) {
            return "<Error! " + e.getMessage() + ">";
        }
    }


    public void setup(OWLModelManager owlModelManager) {
    }


    public void initialise() {
    }
}
