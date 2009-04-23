package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.ui.prefix.PrefixMapper;
import org.protege.editor.owl.ui.prefix.PrefixMapperManager;
import org.semanticweb.owl.model.OWLEntity;

import java.net.URI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 20-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityQNameRenderer extends AbstractOWLEntityRenderer {

    
    public void initialise() {
        // do nothing
    }


    public String render(OWLEntity entity) {
        try {
            PrefixMapper mapper = PrefixMapperManager.getInstance().getMapper();
            String s = mapper.getShortForm(entity.getURI());
            if (s != null) {
                return s;
            }
            else {
                // No mapping
                URI uri = entity.getURI();
                if (uri.getFragment() != null) {
                    return uri.getFragment();
                }
                else {
                    return uri.toString();
                }
            }
        }
        catch (Exception e) {
            return "<Error! " + e.getMessage() + ">";
        }
    }


    protected void disposeRenderer() {
        // do nothing
    }
}
