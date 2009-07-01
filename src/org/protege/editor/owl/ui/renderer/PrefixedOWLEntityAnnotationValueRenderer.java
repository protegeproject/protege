package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.ui.prefix.PrefixMapper;
import org.protege.editor.owl.ui.prefix.PrefixMapperManager;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 18-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PrefixedOWLEntityAnnotationValueRenderer extends OWLEntityAnnotationValueRenderer {

    public String render(OWLEntity entity) {
        String shortForm = getProvider().getShortForm(entity);
        if (OWLRendererPreferences.getInstance().isRenderPrefixes()){
            final String uriStr = entity.getURI().toString();

            PrefixMapper mapper = PrefixMapperManager.getInstance().getMapper();
            for (String base : mapper.getValues()){
                if (uriStr.startsWith(base)){
                    return escape(mapper.getPrefix(base) + ":" + shortForm);
                }
            }
        }
        return escape(shortForm);
    }
}